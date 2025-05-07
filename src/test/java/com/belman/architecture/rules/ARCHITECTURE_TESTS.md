# Architecture Test Suite

| Area                  | Test Class                                                                  |
|-----------------------|-----------------------------------------------------------------------------|
| Clean Architecture    | `rules.clean.CleanArchitectureRulesTest`                                    |
| General Architecture  | `rules.clean.GeneralArchitectureSupportTest`                                |
| DDD Rules             | `rules.ddd.DddArchitectureRulesTest`, `DddPackageStructureRulesTest`        |
| Domain Layer Rules    | `rules.ddd.DomainLayerRulesTest`, `DomainContextsTest`                      |
| Application Layer     | `rules.application.ApplicationLayerRulesTest`                               |
| Infrastructure Layer  | `rules.infrastructure.InfrastructureLayerRulesTest`                         |
| Presentation / MVVM   | `rules.presentation.MVVMAndPresentationRulesTest`                           |
| Naming / Packaging    | `NamingAndPackagingRulesTest` (consider moving to `rules.general`)          |
| Security              | `SecurityRulesTest`                                                         |
| Mobile / Platform     | `PlatformAndGluonRulesTest`                                                 |
