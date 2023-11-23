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
|--------|---------------|-----------------------------------|
| GET    | /scan-ppe     | Se etter verneutstyr i S3 bildene |
| GET    | /scan-tired   | Se etter trøtte folk i S3 bildene |
| POST   | /upload-image | Laster opp bilder til S3 bøtten   |

### Ekstra Endpoints

- `/upload-image`
	- Ett simpelt endpoint for å laste opp bilder.
- `/scan-tired`
	- Skanner etter folk som ser trøtte ut. Helsepersonell som er trøtte har større sjanse i å oppleve enn ulykke, derfor skal denne kunne varsle om noen er trøtt. Rekognition har ingen trøtthet parameter så den bruker forvirret/redd men det burde videre blitt brukt en egen trent modell for dette.

### Telemetri

- Image
	- Total scans
	- Average scans per
		- 10m? 
		- 1h?
	- Uploads
		- Total uploads
		- Upload rate
			- 1h?
- Violations detected
	- Total
		- Total ppe
		- Total exhaustion
	- Rate
- People detected
	- Total
	- Rate

## Drøft

### A. Hva er _kontinuerlig integrasjon_ (CI) er?

Kontinuerlig integrasjon referer til prosessen for hyppig oppdatering av kodebasen og automatisk prosesser for å sikre at endringene er kompatible. 

Kontinuerlig integrasjon (CI) er en utviklingsmetode der all utvikling blir gjort fra en felles kode repo(?) slik at alle utviklerne jobber med en oppdatert versjon av koden. _CI_ prioriterer små og regelmessig oppdateringer for å sikre at alle utviklere jobber med de siste endringene og minimiserer overlapping mellom flere endringer. Dette gir også en detaljert og søkbar versjons historikk. Ett prosjekt jobber som oftest med kodebasen hostet fra tjeneste som GitHub eller Gitlab. Disse tjenestene kommer også med verktøy knyttet til kodebasen sånn som prosjekt planlegging og automatisering av handlinger basert på hendelser i kodeprosjektet som en _commit_ eller _pull request_.

Ett praktisk eksempel for et prosjekt i GitHub med 4 utviklere:
- Utvikler 1 til 4 plukker hver sin _issue_ å jobbe med og lager nye branch-er.
- Utvikler 1 er ferdig med en første versjon av koden som løser issue-en sin og pusher endringen til GitHub og lager en _PR_ mot main branchen.
- Dette automatisk starter en prosess som tester koden med endringen på gjennom _GitHub Actions_.
- Testene er vellykket og PR-en blir ført inn i main.
- Utvikler 4 ser at main har blitt oppdatert og oppdaterer endringene lokalt.
- Utvikler 3 er ferdig og vil pusher endringene sine og lager en PR.
- Koden blir testet automatisk men feiler.
- Utvikler 3 fikser feilen og pusher ny versjon av endringene sine.
- Automatiske tester er vellykket og endringene merget i main
- Utvikler 4 er ferdig med sin issue og pusher koden sin til main

### B. Sammenligning av Scrum/Smidig og DevOps fra et utvikler perspektiv

1. **Scrum/Smidig Metodikk**
   Scrum metodiken baserer seg på rask iterativ utvikling av både prosjekt miljø og produktet. Scrum deler opp prosjektet fremgang gjennom _sprinter_, utviklings tidsenhet, der slutten av en sprint skal kunne levere ett resultat som kan presenteres og vurderes. Denne prosessen kan sammelignes med at kode endringer i DevOps skal kunne integreres og testes så fort som mulig.

2. **DevOps Metodikk**
   DevOps grunnleggende prinsipper er:
   - Flyt: Effektivisering av utviklingsprosessen fra plan til drift med automatisering or reproduserbarhet.
   - Feedback: Bruk telemetri, logger og bruker testing som A/B tester for å få oversikt over produktets effektivitet.
   - Kontinurlig forbedring: Alltid se etter forbedringer i arbeidsprosesser, miljø og verktøy. Herm etter luftfartindustrien, alle hendelser er en mulighet for forbedring.

3. Sammenligning og Kontrast
   Scrum metodikken er mer konsentrert på hva og når noe bli utviklet gjennom å dele funksjonalitet opp i arbeidsoppgaver som skal resultere i ett demonstrerbar produkt innen jevne tidsperioder (sprinter). DevOps derimot er mer fokusert på utviklingsmiljøet, drift og oversikt over ytelsen av funksjonaliteten. Kontinuerlig forbedring er ett kjerneprinsipp i begge metodikkene, det ville ikke vært overraskende med å utføre post-mortem som en del av ett retrospektiv.

### C. Det Andre Prinsippet - Feedback

Med utgangspunkt med at funksjonaliteten har en bit på en frontend, ville de første metrikken være teller for antall brukere og timer for tiden som blir brukt. Dette gir oversikt over interesse av funksjonaliteten. Sammen med metrikker for hvor mye dataressurser funksjonaliteten bruker, kan man få en sammenligning mellom hvor mye verdi funksjonaliteten gir mot hvilke ressurser den koster. Med antagelsen at applikasjonen er kjørt gjennom en skytjeneste, hadde jeg lagt til alarmer knyttet til ressursbruk som går av hvis funksjonaliteten har ressursbruk utenfor forventet nivå. Det gir sjansen til å identifisere utfordringer relatert til skalering for det er for sent.