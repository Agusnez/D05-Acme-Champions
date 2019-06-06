
package services;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Manager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class ManagerServiceTest extends AbstractTest {

	//The SUT----------------------------------------------------
	@Autowired
	private ManagerService			managerService;

	@Autowired
	private ConfigurationService	configurationService;


	/*
	 * ----CALCULATE COVERAGE----
	 * The previous delivery, we calculate it manually. In this one instead we are using the plugin called EclEmma,
	 * with which we can automatically calculate the percentage.
	 * 
	 * Each of the test have their result just before them, and the coverage of the complete test is shown at the end of the document.
	 */

	/*
	 * ACME.CHAMPIONS
	 * a)(Level C) Requirement 9.1: An actor who is not authenticated must be able to register to the system as a manager.
	 * 
	 * b) Negative cases:
	 * 2. Name = blank
	 * 3. Name = null
	 * 
	 * c) Sentence coverage
	 * -create(): 100%
	 * -save(): 66,2%
	 * 
	 * d) Data coverage
	 * -Player: 14,28571%
	 */

	@Test
	public void driverRegisterManager() {
		final Object testingData[][] = {
			{
				"name1", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "managerTest", "managerTest", null
			},//1. All fine
			{
				"		", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "managerTest", "managerTest", ConstraintViolationException.class
			},//2. Name = blank
			{
				null, "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "managerTest", "managerTest", ConstraintViolationException.class
			},//3. Name = null

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateRegisterManager((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6],
				(String) testingData[i][7], (Class<?>) testingData[i][8]);
	}

	protected void templateRegisterManager(final String name, final String surnames, final String photo, final String email, final String phone, final String address, final String username, final String password, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			final Manager manager = this.managerService.create();

			manager.setName(name);
			manager.setSurnames(surnames);
			manager.setPhoto(photo);
			manager.setEmail(email);
			manager.setPhone(phone);
			manager.setAddress(address);

			manager.getUserAccount().setUsername(username);
			manager.getUserAccount().setPassword(password);

			this.managerService.save(manager);
			this.managerService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)(Level C) Requirement 10.2: An actor who is authenticated must be able to edit their personal data.
	 * 
	 * b) Negative cases:
	 * 2. Email = blank
	 * 3. Email = null
	 * 4. Invalid email
	 * 
	 * c) Sentence coverage
	 * -findOne(): 100%
	 * -save(): 30,4%
	 * 
	 * d) Data coverage
	 * -Manager: 14,28571%
	 */

	@Test
	public void driverEditManager() {
		final Object testingData[][] = {
			{
				"name1", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "manager1", "manager1", null
			},//1. All fine
			{
				"name1", "surnames", "https://google.com", "		", "672195205", "address1", "manager1", "manager1", ConstraintViolationException.class
			},//2. Email = blank
			{
				"name1", "surnames", "https://google.com", null, "672195205", "address1", "manager1", "manager1", ConstraintViolationException.class
			},//3. Email = null
			{
				"name1", "surnames", "https://google.com", "noEmail", "672195205", "address1", "manager1", "manager1", ConstraintViolationException.class
			},//4. Invalid email

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateEditManager((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6],
				(String) testingData[i][7], (Class<?>) testingData[i][8]);
	}

	protected void templateEditManager(final String name, final String surnames, final String photo, final String email, final String phone, final String address, final String username, final String bean, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			this.authenticate(username);

			final Manager manager = this.managerService.findOne(super.getEntityId(bean));

			manager.setName(name);
			manager.setSurnames(surnames);
			manager.setPhoto(photo);
			manager.setEmail(email);
			manager.setPhone(phone);
			manager.setAddress(address);

			this.managerService.save(manager);
			this.managerService.flush();

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)(Level C) Requirement 11.4: An actor who is authenticated as president must be able to fire a manager.
	 * 
	 * b) Negative cases:
	 * 2. Invalid authority
	 * 3. Not player
	 * 
	 * c) Sentence coverage
	 * -findOne(): 100%
	 * -save(): 36,3%
	 * 
	 * d) Data coverage
	 * -Player: 0%
	 */

	@Test
	public void driverFireManager() {
		final Object testingData[][] = {
			{
				"president1", "manager1", null
			},//1. All fine
			{
				"spononsor1", "manager1", IllegalArgumentException.class
			},//2. Invalid authority
			{
				"president1", "player1", IllegalArgumentException.class
			},//3. Not player

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateFireManager((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
	}

	protected void templateFireManager(final String usernamePresident, final String beanManager, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			this.authenticate(usernamePresident);

			final Manager manager = this.managerService.findOne(super.getEntityId(beanManager));

			Assert.isTrue(manager != null);

			manager.setTeam(null);

			this.managerService.save(manager);
			this.managerService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)(Level B) Requirement 32.2: An actor who is authenticated as manager must be able to see a prediction of the next matches.
	 * 
	 * b) Negative cases:
	 * 2. Incorrect results
	 * 3. Incorrect results2
	 * 
	 * 
	 * c) Sentence coverage
	 * -findOne(): 100%
	 * 
	 * 
	 * d) Data coverage
	 */
	@Test
	public void driverGoalPrediction() {
		final Object testingData[][] = {

			{
				1.0, null
			},//1. All fine 
			{
				9.999, IllegalArgumentException.class
			},//2. Incorrect results
			{
				-1.0, IllegalArgumentException.class
			},//3. Incorrect results2

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateGoalPrediction((Double) testingData[i][0], (Class<?>) testingData[i][1]);

	}

	protected void templateGoalPrediction(final Double expectedValue, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			super.authenticate("manager1");

			final int id = this.managerService.findByPrincipal().getId();

			final Manager manager = this.managerService.findOne(id);

			final Double result = this.configurationService.goalPrediction(manager.getTeam().getId());

			Assert.isTrue(expectedValue.equals(result));

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();

		}

		this.checkExceptions(expected, caught);

	}

	/*
	 * -------Coverage ManagerService-------
	 * 
	 * ----TOTAL SENTENCE COVERAGE:
	 * ManagerService = 57,1%
	 * 
	 * ----TOTAL DATA COVERAGE:
	 * Manager = 33,33333%
	 */
}
