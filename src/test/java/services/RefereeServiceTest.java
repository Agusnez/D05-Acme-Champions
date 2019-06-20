
package services;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import utilities.AbstractTest;
import domain.Referee;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class RefereeServiceTest extends AbstractTest {

	//The SUT----------------------------------------------------
	@Autowired
	private RefereeService	refereeService;


	/*
	 * ----CALCULATE COVERAGE----
	 * The previous delivery, we calculate it manually. In this one instead we are using the plugin called EclEmma,
	 * with which we can automatically calculate the percentage.
	 * 
	 * Each of the test have their result just before them, and the coverage of the complete test is shown at the end of the document.
	 */

	/*
	 * ACME.CHAMPIONS
	 * a)Requirement 25.2: An actor who is not authenticated must be able to register to the system as a referee.
	 * 
	 * b) Negative cases:
	 * 2. Name = blank
	 * 3. Name = null
	 * 
	 * c) Sentence coverage
	 * -create(): 100%
	 * -save(): 70,6%
	 * 
	 * d) Data coverage
	 * -Referee: 16,66667%
	 */

	@Test
	public void driverRegisterReferee() {
		final Object testingData[][] = {
			{
				"name1", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "refereeTest", "refereeTest", null
			},//1. All fine
			{
				"		", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "refereeTest", "refereeTest", ConstraintViolationException.class
			},//2. Name = blank
			{
				null, "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "refereeTest", "refereeTest", ConstraintViolationException.class
			},//3. Name = null

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateRegisterReferee((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6],
				(String) testingData[i][7], (Class<?>) testingData[i][8]);
	}

	protected void templateRegisterReferee(final String name, final String surnames, final String photo, final String email, final String phone, final String address, final String username, final String password, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			final Referee referee = this.refereeService.create();

			referee.setName(name);
			referee.setSurnames(surnames);
			referee.setPhoto(photo);
			referee.setEmail(email);
			referee.setPhone(phone);
			referee.setAddress(address);

			referee.getUserAccount().setUsername(username);
			referee.getUserAccount().setPassword(password);

			this.refereeService.save(referee);
			this.refereeService.flush();

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
	 * 2. Name = blank
	 * 3. Name = null
	 * 
	 * c) Sentence coverage
	 * -findOne(): 100%
	 * -save(): 28,9%
	 * 
	 * d) Data coverage
	 * -Referee: 16,66667%
	 */

	@Test
	public void driverEditPresident() {
		final Object testingData[][] = {
			{
				"name1", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "referee1", "referee1", null
			},//1. All fine
			{
				"		", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "referee1", "referee1", ConstraintViolationException.class
			},//2. Name = blank
			{
				null, "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "referee1", "referee1", ConstraintViolationException.class
			},//3. Name = null

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateEditReferee((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6],
				(String) testingData[i][7], (Class<?>) testingData[i][8]);
	}
	protected void templateEditReferee(final String name, final String surnames, final String photo, final String email, final String phone, final String address, final String username, final String bean, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			this.authenticate(username);

			final Referee referee = this.refereeService.findOne(super.getEntityId(bean));

			referee.setName(name);
			referee.setSurnames(surnames);
			referee.setPhoto(photo);
			referee.setEmail(email);
			referee.setPhone(phone);
			referee.setAddress(address);

			this.refereeService.save(referee);
			this.refereeService.flush();

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * -------Coverage RefereeService-------
	 * 
	 * ----TOTAL SENTENCE COVERAGE:
	 * RefereeService = 68,1%
	 * 
	 * ----TOTAL DATA COVERAGE:
	 * Referee = 16,66667%
	 */
}
