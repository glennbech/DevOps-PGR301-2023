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
| AWS_ACCOUNT_ID | The account url for ecr repositories e.g `<this part>.dkr.ecr.eu-west-1.amazonaws.com` |
| AWS_ECR         | The name of the ECR repository                                                                                           |

> [!WARNING] 
> The backend in the `infra/` code needs to be configured to run correctly
> 
> Changes needed:
> - `bucket` Is used to store the terraform state. Create a new bucket or use an existing one intended for this
> - `key` Is the file path in the bucket for the state
> - `region` the aws region of the bucket

## `Kjell`

For å teste kjells kode lokalt, bruk disse linjene:

```shell
cd kjell/
docker build -t kjellpy .
docker run -e AWS_ACCESS_KEY_ID=XXX -e AWS_SECRET_ACCESS_KEY=YYY -e BUCKET_NAME=kjellsimagebucket kjellpy
```

## S3RekognitionApplication

| Method | Endpoint      | Description                       |
| ------ | ------------- | --------------------------------- |
| GET    | /scan-ppe     | Se etter verneutstyr i S3 bildene |
| GET    | /scan-tired   | Se etter trøtte folk i S3 bildene |
| POST   | /upload-image | Laster opp bilder til S3 bøtten   |

### Ekstra Endpoints

- `/upload-image`
	- Ett simpelt endpoint for å laste opp bilder.
- `/scan-tired`
	- Skanner etter folk som ser trøtte ut. Helsepersonell som er trøtte har større sjanse i å oppleve enn ulykke, derfor skal denne kunne varsle om noen er trøtt. Rekognition har ingen trøtthet parameter så den bruker forvirret/redd men det burde videre blitt brukt en egen trent modell for dette.