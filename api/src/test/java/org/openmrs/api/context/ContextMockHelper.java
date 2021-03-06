/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.api.context;

import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.mockito.InjectMocks;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.CohortService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.DatatypeService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.ReportService;
import org.openmrs.api.SerializationService;
import org.openmrs.api.UserService;
import org.openmrs.api.VisitService;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.util.RoleConstants;
import org.springframework.context.ApplicationContext;

/**
 * Helps to mock or spy on services. It can be used with {@link InjectMocks}. See
 * {@link org.openmrs.module.ModuleUtilTest} for example. In general you should always try to refactor code first so
 * that this class is not needed. In practice it is mostly enough to replace calls to
 * Context.get...Service with fields, which are injected through a constructor.
 * <p>
 * ContextMockHelper is available in tests extending {@link org.openmrs.test.BaseContextMockTest} and
 * {@link org.openmrs.test.BaseContextSensitiveTest}.
 *
 * @deprecated Avoid using this by not calling Context.get...Service() in your code.
 * @since 1.11, 1.10, 1.9.9
 */
@Deprecated
public class ContextMockHelper {
	
	/**
	 * Mockito does not call setters if there are no fields so you must add both a setter and a
	 * field.
	 */
	AdministrationService administrationService;
	
	CohortService cohortService;
	
	ConceptService conceptService;
	
	DatatypeService datatypeService;
	
	EncounterService encounterService;
	
	FormService formService;
	
	LocationService locationService;
	
	MessageSourceService messageSourceService;
	
	ObsService obsService;
	
	OrderService orderService;
	
	PatientService patientService;
	
	ProviderService providerService;
	
	ReportService reportService;
	
	SerializationService serializationService;
	
	UserService userService;
	
	VisitService visitService;
	
	UserContext userContext;
	
	Map<Class<?>, Object> realServices = new HashMap<Class<?>, Object>();
	
	UserContext realUserContext;
	
	boolean userContextMocked = false;
	
	ApplicationContext applicationContext;
	
	ApplicationContext realApplicationContext;
	
	boolean applicationContextMocked = false;
	
	public ContextMockHelper() {
	}
	
	public void authenticateMockUser() {
		User user = new User();
		user.setUuid("1010d442-e134-11de-babe-001e378eb67e");
		user.setUserId(1);
		user.setUsername("admin");
		user.addRole(new Role(RoleConstants.SUPERUSER));
		
		Person person = new Person();
		person.setUuid("6adb7c42-cfd2-4301-b53b-ff17c5654ff7");
		person.setId(1);
		person.addName(new PersonName("Bob", "", "Smith"));
		Calendar calendar = Calendar.getInstance();
		calendar.set(1980, 01, 01);
		person.setBirthdate(calendar.getTime());
		person.setGender("M");
		user.setPerson(person);
		
		when(userContext.getAuthenticatedUser()).thenReturn(user);
		when(userContext.isAuthenticated()).thenReturn(true);
	}
	
	public void revertMocks() {
		for (Map.Entry<Class<?>, Object> realService : realServices.entrySet()) {
			Context.getServiceContext().setService(realService.getKey(), realService.getValue());
		}
		realServices.clear();
		
		if (userContextMocked) {
			if (realUserContext != null) {
				Context.setUserContext(realUserContext);
				realUserContext = null;
			} else {
				Context.clearUserContext();
			}
			userContextMocked = false;
			userContext = null;
		}
		
		if (applicationContextMocked) {
			if (realApplicationContext != null) {
				Context.getServiceContext().setApplicationContext(realApplicationContext);
				realApplicationContext = null;
			}
			applicationContextMocked = false;
			applicationContext = null;
		}
	}
	
	public void setService(Class<?> type, Object service) {
		if (!realServices.containsKey(type)) {
			Object realService = null;
			try {
				realService = Context.getService(type);
			}
			catch (Exception e) {
				//let's not fail if context is not configured
			}
			
			realServices.put(type, realService);
		}
		
		Context.getServiceContext().setService(type, service);
	}
	
	public void setApplicationContext(ApplicationContext context) {
		if (!applicationContextMocked) {
			realApplicationContext = Context.getServiceContext().getApplicationContext();
			applicationContextMocked = true;
		}
		
		Context.getServiceContext().setApplicationContext(context);
		this.applicationContext = context;
	}
	
	public void setAdministrationService(AdministrationService administrationService) {
		setService(AdministrationService.class, administrationService);
		this.administrationService = administrationService;
	}
	
	public void setCohortService(CohortService cohortService) {
		setService(CohortService.class, cohortService);
		this.cohortService = cohortService;
	}
	
	public void setConceptService(ConceptService conceptService) {
		setService(ConceptService.class, conceptService);
		this.conceptService = conceptService;
	}
	
	public void setDatatypeService(DatatypeService datatypeService) {
		setService(DatatypeService.class, datatypeService);
		this.datatypeService = datatypeService;
	}
	
	public void setEncounterService(EncounterService encounterService) {
		setService(EncounterService.class, encounterService);
		this.encounterService = encounterService;
	}
	
	public void setFormService(FormService formService) {
		setService(FormService.class, formService);
		this.formService = formService;
	}
	
	public void setLocationService(LocationService locationService) {
		setService(LocationService.class, locationService);
		this.locationService = locationService;
	}
	
	public void setMessageSourceService(MessageSourceService messageSourceService) {
		setService(MessageSourceService.class, messageSourceService);
		this.messageSourceService = messageSourceService;
	}
	
	public void setObsService(ObsService obsService) {
		setService(ObsService.class, obsService);
		this.obsService = obsService;
	}
	
	public void setOrderService(OrderService orderService) {
		setService(OrderService.class, orderService);
		this.orderService = orderService;
	}
	
	public void setPatientService(PatientService patientService) {
		setService(PatientService.class, patientService);
		this.patientService = patientService;
	}
	
	public void setProviderService(ProviderService providerService) {
		setService(ProviderService.class, providerService);
		this.providerService = providerService;
	}
	
	public void setReportService(ReportService reportService) {
		setService(ReportService.class, reportService);
		this.reportService = reportService;
	}
	
	public void setSerializationService(SerializationService serializationService) {
		setService(SerializationService.class, serializationService);
		this.serializationService = serializationService;
	}
	
	public void setUserService(UserService userService) {
		setService(UserService.class, userService);
		this.userService = userService;
	}
	
	public void setVisitService(VisitService visitService) {
		setService(VisitService.class, visitService);
		this.visitService = visitService;
	}
	
	public void setUserContext(UserContext userContext) {
		if (!userContextMocked) {
			try {
				realUserContext = Context.getUserContext();
			}
			catch (Exception e) {
				//let's not fail if context is not configured
			}
			
			userContextMocked = true;
		}
		
		Context.setUserContext(userContext);
		this.userContext = userContext;
		authenticateMockUser();
	}
	
}
