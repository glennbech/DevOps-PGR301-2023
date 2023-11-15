# Eksamen PGR301 2023

## Steps to setup repository after fork:

Add the following to the repository:

| Secret                | What                      |
| --------------------- | ------------------------- |
| AWS_ACCESS_KEY_ID     | AWS IAM credential ID     |
| AWS_SECRET_ACCESS_KEY | AWS IAM credential Secret | 

| Variable    | What                                                                    |
| ----------- | ----------------------------------------------------------------------- |
| AWS_REGION  | AWS region for IAM user & cloudformation deployment                     |
| BUCKET_NAME | What the bucket used to store images for _rekognition_ should be called |
| STACK_NAME  | Name of the CloudFormation stack                                        |
