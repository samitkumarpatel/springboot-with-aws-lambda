# proxy.py
from http.server import BaseHTTPRequestHandler, HTTPServer
from urllib.parse import urlparse
import base64, requests, uuid, time

LAMBDA_URL = "http://localhost:9000/2015-03-31/functions/function/invocations"

class Proxy(BaseHTTPRequestHandler):
    def do_GET(self):    self.handle_any("GET")
    def do_POST(self):   self.handle_any("POST")
    def do_PUT(self):    self.handle_any("PUT")
    def do_DELETE(self): self.handle_any("DELETE")
    def do_PATCH(self):  self.handle_any("PATCH")
    def do_HEAD(self):   self.handle_any("HEAD")

    def handle_any(self, method):
        length = int(self.headers.get("Content-Length", 0))
        body = self.rfile.read(length).decode("utf-8", errors="replace") if length else ""

        parsed = urlparse(self.path)
        headers = {k.lower(): v for k, v in self.headers.items()}
        cookies = [c.strip() for c in headers.pop("cookie", "").split(";") if c.strip()]

        event = {
            "version": "2.0",
            "routeKey": "$default",
            "rawPath": parsed.path,
            "rawQueryString": parsed.query,
            "cookies": cookies,
            "headers": headers,
            "requestContext": {
                "accountId": "123456789012",
                "apiId": "local",
                "domainName": "localhost",
                "domainPrefix": "localhost",
                "http": {
                    "method": method,
                    "path": parsed.path,
                    "protocol": "HTTP/1.1",
                    "sourceIp": "127.0.0.1",
                    "userAgent": headers.get("user-agent", "local"),
                },
                "requestId": str(uuid.uuid4()),
                "routeKey": "$default",
                "stage": "$default",
                "time": "01/Jan/2026:00:00:00 +0000",
                "timeEpoch": int(time.time() * 1000),
            },
            "body": body,
            "isBase64Encoded": False,
        }

        r = requests.post(LAMBDA_URL, json=event)
        resp = r.json()
        print(f"=== {method} {self.path} ===")
        print(f"Status: {resp.get('statusCode')}")
        print(f"Headers: {resp.get('headers')}")
        print(f"Cookies: {resp.get('cookies')}")
        print(f"Location: {(resp.get('headers') or {}).get('Location') or (resp.get('headers') or {}).get('location')}")

        self.send_response(resp.get("statusCode", 200))
        for k, v in (resp.get("headers") or {}).items():
            self.send_header(k, v)
        for cookie in (resp.get("cookies") or []):
            self.send_header("Set-Cookie", cookie)
        self.end_headers()

        out = resp.get("body", "") or ""
        if resp.get("isBase64Encoded"):
            out = base64.b64decode(out)
        else:
            out = out.encode("utf-8")
        self.wfile.write(out)

HTTPServer(("0.0.0.0", 8080), Proxy).serve_forever()