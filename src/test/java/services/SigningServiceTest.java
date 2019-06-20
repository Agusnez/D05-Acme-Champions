
package services;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Signing;
import forms.SigningForm;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
public class SigningServiceTest extends AbstractTest {

	//The SUT----------------------------------------------------
	@Autowired
	private SigningService	signingService;


	/*
	 * ----CALCULATE COVERAGE----
	 * The previous delivery, we calculate it manually. In this one instead we are using the plugin called EclEmma,
	 * with which we can automatically calculate the percentage.
	 * 
	 * Each of the test have their result just before them, and the coverage of the complete test is shown at the end of the document.
	 */

	/*
	 * ACME.CHAMPIONS
	 * a)Requirement 27.2: An actor who is authenticated as a president must be able to: Sign a player
	 * 
	 * b) Negative cases:
	 * 2. Wrong authority
	 * 3. No loged actor
	 * 
	 * c) Sentence coverage
	 * -create(): 100%
	 * 
	 * d) Data coverage
	 * -Signing: 0%
	 */

	@Test
	public void driverCreate() {

		final Object testingData[][] = {

			{
				"president1", "player2", null
			}, //1. All fine
			{
				"administrator1", "player2", IllegalArgumentException.class
			}, //2. Wrong authority
			{
				null, "player2", IllegalArgumentException.class
			}, //3. No loged actor

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateCreate((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	protected void templateCreate(final String actorBean, final String player, final Class<?> expected) {

		Class<?> caught;

		caught = null;

		try {

			this.startTransaction();

			super.authenticate(actorBean);

			final SigningForm signing = this.signingService.create(super.getEntityId(player));

			Assert.notNull(signing);

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.unauthenticate();

		this.rollbackTransaction();

		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)Requirement 27.2: An actor who is authenticated as a president must be able to: Sign a player
	 * 
	 * b) Negative cases:
	 * 2. Wrong authority
	 * 3. No loged actor
	 * 
	 * c) Sentence coverage
	 * -findByPresidentId(): 100%
	 * 
	 * d) Data coverage
	 * -Signing: 0%
	 */

	@Test
	public void driverList() {

		final Object testingData[][] = {

			{
				"president1", null
			}, //1. All fine
			{
				"administrator1", IllegalArgumentException.class
			}, //2. Wrong authority
			{
				"referee1", IllegalArgumentException.class
			}, //3. No loged actor

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateList((String) testingData[i][0], (Class<?>) testingData[i][1]);

	}

	protected void templateList(final String actorBean, final Class<?> expected) {

		Class<?> caught;

		caught = null;

		try {

			this.startTransaction();

			super.authenticate(actorBean);

			final Collection<Signing> signing = this.signingService.findByPresident(super.getEntityId(actorBean));

			Assert.notNull(signing);

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.unauthenticate();

		this.rollbackTransaction();

		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)Requirement 27.2: An actor who is authenticated as a president must be able to: Hire a player
	 * 
	 * b) Negative cases:
	 * 2. Wrong authority
	 * 3. No loged actor
	 * 
	 * c) Sentence coverage
	 * -save(): 46,2%
	 * -reconstruct():51,2%
	 * 
	 * d) Data coverage
	 * -Signing: 0%
	 */

	@Test
	public void driverSave() {

		final Object testingData[][] = {

			{
				"president1", "player2", null
			}, //1. All fine
			{
				"administrator1", "player2", IllegalArgumentException.class
			}, //2. Wrong authority
			{
				null, "player2", IllegalArgumentException.class
			}, //3. No loged actor

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateSave((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	protected void templateSave(final String actorBean, final String player, final Class<?> expected) {

		Class<?> caught;

		caught = null;

		try {

			this.startTransaction();

			super.authenticate(actorBean);

			final SigningForm signingForm = this.signingService.create(super.getEntityId(player));

			signingForm.setPrice(150000.50);
			signingForm.setOfferedClause(150000.50);

			final Signing signing = this.signingService.reconstruct(signingForm, null);

			final Signing saved = this.signingService.save(signing);

			Assert.notNull(saved);

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.unauthenticate();

		this.rollbackTransaction();

		super.checkExceptions(expected, caught);

	}

	/*
	 * -------Coverage SigningService-------
	 * 
	 * ----TOTAL SENTENCE COVERAGE:
	 * SigningService = 44,1%
	 * 
	 * ----TOTAL DATA COVERAGE:
	 * Signing = 0%
	 */
}
