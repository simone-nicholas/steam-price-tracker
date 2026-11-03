# Steam Price Tracker

A Spring Boot backend that reads game prices from Steam and, in the future, notifies the user when a price drops below a threshold they set.

The project started as a way to learn Spring Boot: the goal is to build a useful, complete application featuring an external HTTP client, scheduled jobs, stateful domain logic, JWT security, tests with fake external dependencies, and notifications. The iOS app comes last, once the backend is complete and tested.

> **Status:** under development, Phase 1 (reading a price). It is not yet usable as an alerting service.

## How it will work (end goal)

1. The user picks a Steam game and sets a target price.
2. A periodic job checks prices and stores their history.
3. When the price drops below the target, the user gets a notification (email first, then iOS push).

The alert rules (notify immediately if the price is already below the target, send one alert or several, re-notify after the price rises and drops again) are domain decisions to be settled and covered by tests in Phase 4.

## Current state

Already working:

- calling the Steam store details endpoint with `RestClient`;
- mapping Steam's JSON into Java records;
- a REST endpoint that, given a game's `appId`, returns its name and current price for the Italian region (EUR).

Not yet implemented: persistence, scheduler, alert logic, notifications, authentication, tests for the Steam client.

## Requirements

- JDK 25 or later
- Maven (the `./mvnw` wrapper is included)
- Internet connection (prices are read from Steam)

## Running

```bash
./mvnw spring-boot:run
```

The application starts on `http://localhost:8080`.

## API

### Game price

```
GET /api/v1/games/{appId}/price
```

Example with F1® 25 (`appId` 3059520):

```bash
curl http://localhost:8080/api/v1/games/3059520/price
```

Response (current format, which still mirrors Steam's structure and will change once the domain type is introduced):

```json
{
  "success": true,
  "data": {
    "name": "F1® 25",
    "is_free": false,
    "price_overview": {
      "currency": "EUR",
      "initial": 4999,
      "final": 4999,
      "discount_percent": 0,
      "initial_formatted": "",
      "final_formatted": "49,99€"
    }
  }
}
```

Prices are expressed in **cents** (`4999` = €49.99).

## Configuration

In `src/main/resources/application.properties`:

| Property | Description | Default |
|---|---|---|
| `server.port` | Server port | `8080` |
| `steam.api.base-url` | Base URL of the Steam store API | `https://store.steampowered.com/api` |

The region (`cc=it`) and language (`l=italian`) are currently hardcoded in the client: the project deals with a single region and a single currency.

## Data source

Prices are read from the `store.steampowered.com/api/appdetails` endpoint, which is **not officially documented by Valve** and has undeclared rate limits. For this reason the source will be hidden behind an interface (`PriceProvider`), with caching and a single job that queries Steam on behalf of all users. If Steam changes or restricts the endpoint, the source can be swapped without touching the rest of the application.

This project is not affiliated with or endorsed by Valve Corporation. Steam and the Steam logo are registered trademarks of Valve Corporation.

## Project structure

```
com.company.steampricetracker
└── Steam          (client, service, controller and DTOs for the Steam API)
```

Planned structure as the phases progress:

```
alert · game · price · notification · scheduler · security · common
```

## Roadmap

- [ ] **Phase 1**: read a price, no database *(in progress)*
- [ ] **Phase 2**: persistence (`Game`, `PricePoint`, `Alert`) with JPA
- [ ] **Phase 3**: periodic checks with `@Scheduled` and price history
- [ ] **Phase 4**: alert logic, tested with price sequences
- [ ] **Phase 5**: email notifications (`NotificationChannel`)
- [ ] **Phase 6**: registration, login and JWT; each user sees only their own alerts
- [ ] **Phase 7**: error handling, caching, OpenAPI/Swagger, Docker and deployment
- [ ] **Phase 8**: iOS app and push notifications (APNs)

One phase at a time: the next one starts only when the previous phase's "done when" criteria are met, and each phase ends with a working commit.

## Tech stack

- Java, Spring Boot 4.1
- Spring Web MVC, `RestClient`
- Maven

Planned for later phases: Spring Data JPA, PostgreSQL, Spring Security (JWT), WireMock, Mailpit/GreenMail, Docker.

## Testing strategy

| What | How |
|---|---|
| Threshold logic | Unit tests with price sequences (e.g. 50, 50, 30, 50, 30) |
| Steam client | WireMock with recorded responses, no network |
| Email | GreenMail or Mailpit |
| Scheduler | Call the method directly in tests |
| Full chain | Manual test with a threshold above the current price |

## License

To be decided.# steam-price-tracker
