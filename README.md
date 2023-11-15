# Eksamen PGR301 2023

## Hvordan Sette Opp Fork Av Kodebasen:

Legg til disse verdiene på som repository secrets

| Secret                |
| --------------------- |
| AWS_ACCESS_KEY_ID     |
| AWS_SECRET_ACCESS_KEY |

repository variables (ligger på samme sted som repository secrets)

| Variabel        | Skal inneholde                                                                             |
| --------------- | ------------------------------------------------------------------------------------------ |
| AWS_REGION      | AWS region for IAM bruker & Cloudformation ️distribusjonen                                 |
| BUCKET_NAME     | Hva S3 bøtten som skal lagre bildene skal hete                                             |
| STACK_NAME      | Navn på CloudFormation stacken                                                             |
| AWS_ACCOUNT_URL | The account url for ecr repositories e.g `<account_number>.dkr.ecr.<region>.amazonaws.com` |
| AWS_ECR         | The name of the ECR repository                                                                                           |

## `Kjell`

For å teste kjells kode lokalt, bruk disse linjene:

```shell
cd kjell/
docker build -t kjellpy .
docker run -e AWS_ACCESS_KEY_ID=XXX -e AWS_SECRET_ACCESS_KEY=YYY -e BUCKET_NAME=kjellsimagebucket kjellpy
```