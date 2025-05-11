# Business Layer Implementation Task List

## Core Business Layer Components

- [x] Create AuditEvent.java based on DomainEvent.java
- [x] Create BaseAuditEvent.java based on AbstractDomainEvent.java
- [x] Create IAuditPublisher.java based on IDomainEventPublisher.java
- [x] Create AuditHandler.java based on DomainEventHandler.java
- [x] Create AuditHandlerImplementation.java based on DomainEventHandlerImplementation.java
- [x] Create AuditPublisher.java based on DomainEventPublisher.java
- [x] Create BusinessObject.java based on AggregateRoot.java and Aggregate.java
- [x] Create BusinessComponent.java based on Entity.java
- [x] Create IBusinessService.java based on IDomainService.java
- [x] Create BusinessService.java based on DomainService.java
- [x] Create DataAccessInterface.java based on Repository.java
- [x] Create DataObject.java based on ValueObject.java
- [x] Create BusinessException.java based on DomainException.java

## Update Existing Business Entities

- [ ] Update OrderBusiness.java (renamed from OrderAggregate.java) to use BusinessObject
- [ ] Update UserBusiness.java (renamed from UserAggregate.java) to use BusinessObject
- [ ] Update CustomerBusiness.java (renamed from CustomerAggregate.java) to use BusinessObject
- [ ] Update ReportBusiness.java (renamed from ReportAggregate.java) to use BusinessObject

## Update Existing Business Components

- [ ] Update Photo.java to use BusinessComponent
- [ ] Update other business components to use BusinessComponent

## Update Existing Data Objects

- [ ] Update EmailAddress.java to use DataObject
- [ ] Update Money.java to use DataObject
- [ ] Update PersonName.java to use DataObject
- [ ] Update PhoneNumber.java to use DataObject
- [ ] Update Timestamp.java to use DataObject
- [ ] Update other data objects to use DataObject

## Update Existing Business Services

- [ ] Update PhotoApprovalService.java to use BusinessService
- [ ] Update PhotoValidationService.java to use BusinessService
- [ ] Update PhotoReportGenerationService.java to use BusinessService
- [ ] Update OrderBusinessService.java (renamed from OrderDomainService.java) to use BusinessService
- [ ] Update other business services to use BusinessService

## Update Existing Data Access Interfaces

- [ ] Update OrderDataAccess.java (renamed from OrderRepository.java) to use DataAccessInterface
- [ ] Update PhotoDataAccess.java (renamed from PhotoRepository.java) to use DataAccessInterface
- [ ] Update UserDataAccess.java (renamed from UserRepository.java) to use DataAccessInterface
- [ ] Update CustomerDataAccess.java (renamed from CustomerRepository.java) to use DataAccessInterface
- [ ] Update ReportDataAccess.java (renamed from ReportRepository.java) to use DataAccessInterface

## Update Existing Exceptions

- [ ] Update AccessDeniedException.java to extend BusinessException
- [ ] Update BusinessRuleViolationException.java to extend BusinessException
- [ ] Update EntityNotFoundException.java to extend BusinessException
- [ ] Update InvalidValueException.java to extend BusinessException

## Update Existing Audit Events

- [ ] Update OrderCreatedEvent.java to use BaseAuditEvent
- [ ] Update OrderApprovedEvent.java to use BaseAuditEvent
- [ ] Update OrderRejectedEvent.java to use BaseAuditEvent
- [ ] Update OrderCompletedEvent.java to use BaseAuditEvent
- [ ] Update OrderCancelledEvent.java to use BaseAuditEvent
- [ ] Update PhotoApprovedEvent.java to use BaseAuditEvent
- [ ] Update PhotoRejectedEvent.java to use BaseAuditEvent
- [ ] Update UserCreatedEvent.java to use BaseAuditEvent
- [ ] Update UserApprovedEvent.java to use BaseAuditEvent
- [ ] Update UserRejectedEvent.java to use BaseAuditEvent
- [ ] Update ReportGeneratedEvent.java to use BaseAuditEvent
- [ ] Update ReportCompletedEvent.java to use BaseAuditEvent
- [ ] Update other audit events to use BaseAuditEvent

## Update Tests

- [x] Update OrderAggregateTest.java to test OrderBusiness
- [x] Update UserAggregateTest.java to test UserBusiness (Note: Attempted to create UserBusinessTest.java but
  encountered dependency rule violations)
- [ ] Update CustomerAggregateTest.java to test CustomerBusiness
- [ ] Update ReportAggregateTest.java to test ReportBusiness
- [ ] Update PhotoTemplateTest.java to test PhotoTemplate
- [ ] Update EmailAddressTest.java to test EmailAddress
- [ ] Update PhoneNumberTest.java to test PhoneNumber
- [ ] Update PersonNameTest.java to test PersonName
- [ ] Update HashedPasswordTest.java to test HashedPassword
- [ ] Update other tests to use the new naming conventions

## Update Architecture Guide

- [ ] Update ARCHITECTURE_GUIDE.md to remove DDD terminology and use the new naming conventions
