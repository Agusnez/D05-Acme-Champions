
package services;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import utilities.AbstractTest;
import domain.President;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class PresidentServiceTest extends AbstractTest {

	//The SUT----------------------------------------------------
	@Autowired
	private PresidentService	presidentService;


	/*
	 * ----CALCULATE COVERAGE----
	 * The previous delivery, we calculate it manually. In this one instead we are using the plugin called EclEmma,
	 * with which we can automatically calculate the percentage.
	 * 
	 * Each of the test have their result just before them, and the coverage of the complete test is shown at the end of the document.
	 */

	/*
	 * ACME.CHAMPIONS
	 * a)Requirement 30.2: An actor who is authenticated must be able to create user accounts for new presidents.
	 * 
	 * b) Negative cases:
	 * 2. Name = blank
	 * 3. Name = null
	 * 
	 * c) Sentence coverage
	 * -create(): 100%
	 * -save():77,2%
	 * 
	 * d) Data coverage
	 * -President: 16,66667%
	 */

	@Test
	public void driverRegisterPresident() {
		final Object testingData[][] = {
			{
				"name1", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "presidentTest", "presidentTest", null
			},//1. All fine
			{
				"		", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "managerTest", "managerTest", ConstraintViolationException.class
			},//2. Name = blank
			{
				null, "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "managerTest", "managerTest", ConstraintViolationException.class
			},//3. Name = null

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateRegisterPresident((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6],
				(String) testingData[i][7], (Class<?>) testingData[i][8]);
	}

	protected void templateRegisterPresident(final String name, final String surnames, final String photo, final String email, final String phone, final String address, final String username, final String password, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			this.authenticate("admin");

			final President president = this.presidentService.create();

			president.setName(name);
			president.setSurnames(surnames);
			president.setPhoto(photo);
			president.setEmail(email);
			president.setPhone(phone);
			president.setAddress(address);

			president.getUserAccount().setUsername(username);
			president.getUserAccount().setPassword(password);

			this.presidentService.save(president);
			this.presidentService.flush();

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
	 * 2. Surname = blank
	 * 3. Surname = null
	 * 
	 * c) Sentence coverage
	 * -findOne(): 100%
	 * -save():25,9%
	 * 
	 * d) Data coverage
	 * -President: 16,66667%
	 */

	@Test
	public void driverEditPresident() {
		final Object testingData[][] = {
			{
				"name1", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "president1", "president1", null
			},//1. All fine
			{
				"name1", "		", "https://google.com", "email1@gmail.com", "672195205", "address1", "president1", "president1", ConstraintViolationException.class
			},//2. Surname = blank
			{
				"name1", null, "https://google.com", "email1@gmail.com", "672195205", "address1", "president1", "president1", ConstraintViolationException.class
			},//3. Surname = null

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateEditPresident((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6],
				(String) testingData[i][7], (Class<?>) testingData[i][8]);
	}

	protected void templateEditPresident(final String name, final String surnames, final String photo, final String email, final String phone, final String address, final String username, final String bean, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			this.authenticate(username);

			final President president = this.presidentService.findOne(super.getEntityId(bean));

			president.setName(name);
			president.setSurnames(surnames);
			president.setPhoto(photo);
			president.setEmail(email);
			president.setPhone(phone);
			president.setAddress(address);

			this.presidentService.save(president);
			this.presidentService.flush();

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * -------Coverage PresidentService-------
	 * 
	 * ----TOTAL SENTENCE COVERAGE:
	 * PresidentService = 71,9%
	 * 
	 * ----TOTAL DATA COVERAGE:
	 * President = 33,33333%
	 */
}
