import boto3
import json
import logging
import schedule

# Configure logger with appropriate level and output destination
logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)
logging.basicConfig(filename='lambda_trigger.log', format='%(asctime)s - %(levelname)s - %(message)s')

# Store Lambda function name and region securely using environment variables
LAMBDA_FUNCTION_NAME = os.environ.get('LAMBDA_FUNCTION_NAME')
REGION_NAME = os.environ.get('AWS_REGION')

# Ensure environment variables are set
if not LAMBDA_FUNCTION_NAME or not REGION_NAME:
    logger.error(
        "Missing environment variables. Please set LAMBDA_FUNCTION_NAME and AWS_REGION."
    )
    raise ValueError("Missing environment variables")

# Load custom payload data (securely handle sensitive information)
try:
    with open('payload.json', 'r') as f:
        payload_data = json.load(f)
except FileNotFoundError:
    logger.error("payload.json file not found. Please provide a valid payload file.")
    raise FileNotFoundError("Missing payload data")

# Create custom credential provider (consider IAM roles for improved security)
def custom_credential_provider():
    # Implement secure credential retrieval (e.g., from AWS Secrets Manager)
    ...
    return credentials

# Create Boto3 client with custom credential provider
session = boto3.Session(
    aws_access_key_id='YOUR_ACCESS_KEY_ID',  # Replace with actual credentials (optional)
    aws_secret_access_key='YOUR_SECRET_ACCESS_KEY',  # Replace with actual credentials (optional)
    region_name=REGION_NAME,
    config=botocore.config.Config(credential_provider=custom_credential_provider)
)
lambda_client = session.client('lambda')

# Function to send Lambda invocation with custom payload
def trigger_lambda():
    try:
        response = lambda_client.invoke(
            FunctionName=LAMBDA_FUNCTION_NAME,
            InvocationType='RequestResponse',
            Payload=json.dumps(payload_data)
        )
        status_code = response.get('StatusCode')
        if status_code != 200:
            logger.warning(f"Lambda invocation failed with status code: {status_code}")
        else:
            logger.info("Lambda invocation successful!")
    except Exception as e:
        logger.error(f"Error invoking Lambda: {e}")

# Schedule Lambda invocation every 15 minutes
schedule.every(15).minutes.do(trigger_lambda)

while True:
    schedule.run_pending()
    time.sleep(1)
