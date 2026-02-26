import json
import boto3
import pymysql
import os
import ssl

SECRET_NAME = "MySQLDB11Secret"
REGION_NAME = "us-east-1"
PROXY_ENDPOINT = "db11proxy.proxy-cyj8oim2kbnw.us-east-1.rds.amazonaws.com"
DB_NAME = "database-11"

def lambda_handler(event, context):
    print("Lambda execution started")

    # 1. Retrieve DB credentials from Secrets Manager
    try:
        print("Retrieving DB credentials...")
        session = boto3.session.Session()
        client = session.client("secretsmanager", region_name=REGION_NAME)
        secret_value = client.get_secret_value(SecretId=SECRET_NAME)
        creds = json.loads(secret_value["SecretString"])
        username = creds["username"]
        password = creds["password"]
        print("Successfully retrieved credentials")
    except Exception as e:
        print(f"Secrets Manager error: {e}")
        return {"statusCode": 500, "body": json.dumps({"error": str(e)})}

    # 2. Attempt secure TLS connection using system trust store
    try:
        print("Attempting secure TLS connection with system trust store...")
        ctx = ssl.create_default_context()  # Uses Lambda runtime's CA bundle
        ctx.check_hostname = True
        ctx.verify_mode = ssl.CERT_REQUIRED

        conn = pymysql.connect(
            host=PROXY_ENDPOINT,
            user=username,
            password=password,
            database=DB_NAME,
            port=3306,
            connect_timeout=5,
            ssl={"ssl": ctx}
        )
        print("✅ Connected securely with certificate verification")
    except Exception as e:
        print(f"System trust store connection failed: {e}")
        print("⚠ Retrying with TLS but skipping certificate verification (less secure)...")

        try:
            # Create TLS context with verification disabled
            ctx = ssl.SSLContext(ssl.PROTOCOL_TLS_CLIENT)
            ctx.check_hostname = False
            ctx.verify_mode = ssl.CERT_NONE

            conn = pymysql.connect(
                host=PROXY_ENDPOINT,
                user=username,
                password=password,
                database=DB_NAME,
                port=3306,
                connect_timeout=5,
                ssl={"ssl": ctx}
            )
            print("✅ Connected with TLS but verification disabled")
        except Exception as e2:
            print(f"❌ Connection failed even with verification disabled: {e2}")
            return {
                "statusCode": 500,
                "body": json.dumps({"error": str(e2)})
            }

    # 3. Run a test query
    try:
        with conn.cursor() as cursor:
            cursor.execute("SELECT NOW();")
            result = cursor.fetchone()
            print(f"Query result: {result}")
        conn.close()
        return {"statusCode": 200, "body": json.dumps({"current_time": str(result[0])})}
    except Exception as e:
        print(f"Query error: {e}")
        return {"statusCode": 500, "body": json.dumps({"error": str(e)})}
