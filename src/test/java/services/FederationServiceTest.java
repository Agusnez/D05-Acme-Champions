
package services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import utilities.AbstractTest;
import domain.Federation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class FederationServiceTest extends AbstractTest {

	//The SUT----------------------------------------------------
	@Autowired
	private FederationService	federationService;


	/*
	 * ----CALCULATE COVERAGE----
	 * The previous delivery, we calculate it manually. In this one instead we are using the plugin called EclEmma,
	 * with which we can automatically calculate the percentage.
	 * 
	 * Each of the test have their result just before them, and the coverage of the complete test is shown at the end of the document.
	 */

	/*
	 * ACME.CHAMPIONS
	 * a)Requirement 25.4: An actor who is not authenticated must be able to register to the system as a federation.
	 * 
	 * b) Negative cases:
	 * 2. Name = blank
	 * 3. Name = null
	 * 
	 * c) Sentence coverage
	 * -create(): 100%
	 * -save(): 66,8%
	 * 
	 * d) Data coverage
	 * -Federation: 14,28571%
	 */

	@Test
	public void driverRegisterFederation() {
		final Object testingData[][] = {
			{
				"name1", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "1998/06/29", "federationTest", "federationTest", null
			},//1. All fine
			{
				"		", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "1998/06/29", "federationTest", "federationTest", ConstraintViolationException.class
			},//2. Name = blank
			{
				null, "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "1998/06/29", "federationTest", "federationTest", ConstraintViolationException.class
			},//3. Name = null

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateRegisterFederation((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5],
				this.convertStringToDate((String) testingData[i][6]), (String) testingData[i][7], (String) testingData[i][8], (Class<?>) testingData[i][9]);
	}

	protected void templateRegisterFederation(final String name, final String surnames, final String photo, final String email, final String phone, final String address, final Date establishmentDate, final String username, final String password,
		final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			final Federation federation = this.federationService.create();

			federation.setName(name);
			federation.setSurnames(surnames);
			federation.setPhoto(photo);
			federation.setEmail(email);
			federation.setPhone(phone);
			federation.setAddress(address);
			federation.setEstablishmentDate(establishmentDate);

			federation.getUserAccount().setUsername(username);
			federation.getUserAccount().setPassword(password);

			this.federationService.save(federation);
			this.federationService.flush();

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)Requirement 26.2: An actor who is authenticated must be able to edit their personal data.
	 * 
	 * b) Negative cases:
	 * 2. EstablishmentDate = null
	 * 3. EstablishmentDate = not past
	 * 
	 * c) Sentence coverage
	 * -findOne(): 100%
	 * -save(): 31,0%
	 * 
	 * d) Data coverage
	 * -Federation: 14,28571%
	 */

	@Test
	public void driverEditFederation() {
		final Object testingData[][] = {
			{
				"name1", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "1998/06/29", "federation1", "federation1", null
			},//1. All fine
			{
				"name1", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", null, "federation1", "federation1", ConstraintViolationException.class
			},//2. EstablishmentDate = null
			{
				"name1", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "2050/06/29", "federation1", "federation1", ConstraintViolationException.class
			},//3. EstablishmentDate = not past

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateEditFederation((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5],
				this.convertStringToDate((String) testingData[i][6]), (String) testingData[i][7], (String) testingData[i][8], (Class<?>) testingData[i][9]);
	}
	protected void templateEditFederation(final String name, final String surnames, final String photo, final String email, final String phone, final String address, final Date establishmentDate, final String username, final String bean,
		final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			this.authenticate(username);

			final Federation federation = this.federationService.findOne(super.getEntityId(bean));

			federation.setName(name);
			federation.setSurnames(surnames);
			federation.setPhoto(photo);
			federation.setEmail(email);
			federation.setPhone(phone);
			federation.setAddress(address);
			federation.setEstablishmentDate(establishmentDate);

			this.federationService.save(federation);
			this.federationService.flush();

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	//Methods

	protected Date convertStringToDate(final String dateString) {
		Date date = null;

		if (dateString != null) {
			final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
			try {
				date = df.parse(dateString);
			} catch (final Exception ex) {
				System.out.println(ex);
			}
		}

		return date;
	}

	/*
	 * -------Coverage FederationService-------
	 * 
	 * ----TOTAL SENTENCE COVERAGE:
	 * FederationService = 69,1%
	 * 
	 * ----TOTAL DATA COVERAGE:
	 * Federation = 28,57143%
	 */
}
