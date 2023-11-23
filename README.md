# Eksamen PGR301 2023

## Hvordan Sette Opp Fork Av Kodebasen:

Legg til disse verdiene på som repository secrets

| Secret                |
| --------------------- |
| AWS_ACCESS_KEY_ID     |
| AWS_SECRET_ACCESS_KEY |

repository variables (ligger på samme sted som repository secrets)

| Variabel       | Skal inneholde                                                                          |
| -------------- | --------------------------------------------------------------------------------------- |
| AWS_REGION     | AWS region for IAM bruker & Cloudformation ️distribusjonen                              |
| BUCKET_NAME    | Hva S3 bøtten som skal lagre bildene skal hete                                          |
| STACK_NAME     | Navn på CloudFormation stacken                                                          |
| AWS_ACCOUNT_ID | AWS konto id for ecr repositories d.v.s `<denne biten>.dkr.ecr.eu-west-1.amazonaws.com` |
| AWS_ECR        | The name of the ECR repository                                                          |

> [!WARNING] 
> Backend-en i `infra/` trenger disse endringene for å fungere:
> 
> - `bucket` brukes for å spore infrastrukturens oppsett. Lag en _bucket_ eller bruk en eksisterende for det formålet
> - `key` er filnavnet der tilstanden blir lagret i bucket-en
> - `region` AWS regionen for _bucket_-en

## `Kjell`

For å teste koden lokalt, bruk disse linjene:

```shell
cd kjell/
docker build -t kjellpy .
docker run -e AWS_ACCESS_KEY_ID=XXX -e AWS_SECRET_ACCESS_KEY=YYY -e BUCKET_NAME=kjellsimagebucket kjellpy
```

---

## `S3RekognitionApplication`

For å teste koden bruk disse linjene:

```shell
docker build -t ppe . 
docker run -p 8080:8080 -e AWS_ACCESS_KEY_ID=XXX -e AWS_SECRET_ACCESS_KEY=YYY -e BUCKET_NAME=kjellsimagebucket ppe
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

### Telemetri️

- Images scanned
- Violations found
- Scan requests
