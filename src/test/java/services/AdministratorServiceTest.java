
package services;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import utilities.AbstractTest;
import domain.Administrator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class AdministratorServiceTest extends AbstractTest {

	//The SUT--------------------------------------------------
	@Autowired
	private AdministratorService	adminService;


	/*
	 * ----CALCULATE COVERAGE----
	 * The previous delivery, we calculate it manually. In this one instead we are using the plugin called EclEmma,
	 * with which we can automatically calculate the percentage.
	 * 
	 * Each of the test have their result just before them, and the coverage of the complete test is shown at the end of the document.
	 */

	/*
	 * ACME.CHAMPIONS
	 * a)(Level C) Requirement 30.1: An actor who is authenticated must be able to create user accounts for new administrators.
	 * 
	 * b) Negative cases:
	 * 2. Name = blank
	 * 3. Name = null
	 * 
	 * c) Sentence coverage
	 * -create(): 100%
	 * -save():72%
	 * 
	 * d) Data coverage
	 * -Administrator: 16,66667%
	 */

	@Test
	public void driverRegisterAdmin() {
		final Object testingData[][] = {
			{
				"name1", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "adminTest", "adminTest", null
			},//1. All fine
			{
				"		", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "adminTest", "adminTest", ConstraintViolationException.class
			},//2. Name = blank
			{
				null, "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "adminTest", "adminTest", ConstraintViolationException.class
			},//3. Name = null

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateRegisterAdmin((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6],
				(String) testingData[i][7], (Class<?>) testingData[i][8]);
	}

	protected void templateRegisterAdmin(final String name, final String surnames, final String photo, final String email, final String phone, final String address, final String username, final String password, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			this.authenticate("admin");

			final Administrator admin = this.adminService.create();

			admin.setName(name);
			admin.setSurnames(surnames);
			admin.setPhoto(photo);
			admin.setEmail(email);
			admin.setPhone(phone);
			admin.setAddress(address);

			admin.getUserAccount().setUsername(username);
			admin.getUserAccount().setPassword(password);

			this.adminService.save(admin);
			this.adminService.flush();

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)(Level C) Requirement 26.2: An actor who is authenticated must be able to edit their personal data.
	 * 
	 * b) Negative cases:
	 * 2. Surname = blank
	 * 3. Surname = null
	 * 
	 * c) Sentence coverage
	 * -findOne(): 100%
	 * -save():30,3%
	 * 
	 * d) Data coverage
	 * -Administrator: 16,66667%
	 */

	@Test
	public void driverEditAdmin() {
		final Object testingData[][] = {
			{
				"name1", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "admin", "administrator1", null
			},//1. All fine
			{
				"name1", "		", "https://google.com", "email1@gmail.com", "672195205", "address1", "admin", "administrator1", ConstraintViolationException.class
			},//2. Surname = blank
			{
				"name1", null, "https://google.com", "email1@gmail.com", "672195205", "address1", "admin", "administrator1", ConstraintViolationException.class
			},//3. Surname = null

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateEditAdmin((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6],
				(String) testingData[i][7], (Class<?>) testingData[i][8]);
	}
	protected void templateEditAdmin(final String name, final String surnames, final String photo, final String email, final String phone, final String address, final String username, final String bean, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			this.authenticate(username);

			final Administrator admin = this.adminService.findOne(super.getEntityId(bean));

			admin.setName(name);
			admin.setSurnames(surnames);
			admin.setPhoto(photo);
			admin.setEmail(email);
			admin.setPhone(phone);
			admin.setAddress(address);

			this.adminService.save(admin);
			this.adminService.flush();

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * -------Coverage AdministratorService-------
	 * 
	 * ----TOTAL SENTENCE COVERAGE:
	 * AdministratorService = 71,5%
	 * 
	 * ----TOTAL DATA COVERAGE:
	 * Administrator = 33,33333%
	 */

}
