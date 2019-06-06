
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
import domain.Sponsorship;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class SponsorshipServiceTest extends AbstractTest {

	//The SUT----------------------------------------------------

	@Autowired
	private SponsorshipService	sponsorshipService;

	@Autowired
	private ActorService		actorService;


	/*
	 * ----CALCULATE COVERAGE----
	 * The previous delivery, we calculate it manually. In this one instead we are using the plugin called EclEmma,
	 * with which we can automatically calculate the percentage.
	 * 
	 * Each of the test have their result just before them, and the coverage of the complete test is shown at the end of the document.
	 */

	/*
	 * ACME-CHAMPIONS
	 * a)(Level A) Requirement 43.1: Actors that are authenticated as a sponsor must be able to: Create sponsorship
	 * 
	 * b) Negative cases:
	 * 2. Wrong authority
	 * 3. Banner is not an URL
	 * 
	 * c) Sentence coverage
	 * -save() = 86.3%
	 * -createWithTeam():98.1%
	 * 
	 * 
	 * d) Data coverage
	 * -Sponsorship = 0%
	 */

	@Test
	public void driverCreateSponsorsip() {
		final Object testingData[][] = {

			{
				"team1", "sponsor1", "http://google2.com", "http://google.com", null
			},//1. All fine
			{
				"team1", "manager", "http://google.com", "http://google.com", IllegalArgumentException.class
			},//2. Wrong authority
			{
				"team1", "sponsor1", "banner1", "http://google.com", ConstraintViolationException.class
			},//3. Banner is not an URL

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateCreateSponsorship((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Class<?>) testingData[i][4]);

	}
	protected void templateCreateSponsorship(final String teamId, final String username, final String banner, final String targetUrl, final Class<?> expected) {
		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			this.authenticate(username);

			final Sponsorship sponsorship = this.sponsorshipService.createWithTeam(super.getEntityId(teamId));

			sponsorship.setBanner(banner);
			sponsorship.setTarget(targetUrl);

			this.sponsorshipService.save(sponsorship);
			this.sponsorshipService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();

		}

		this.unauthenticate();

		this.rollbackTransaction();

		super.checkExceptions(expected, caught);
	}

	/*
	 * ACME-CHAMPIONS
	 * a)(Level A) Requirement 43.1: Actors that are authenticated as a sponsor must be able to: List his/her sponsorships
	 * 
	 * b) Negative cases:
	 * 2. Incorrect results
	 * 3. Wrong authority
	 * 
	 * c) Sentence coverage
	 * -findAllBySponsorId():100%
	 * 
	 * 
	 * d) Data coverage
	 * -Sponsorship = 0%
	 */

	@Test
	public void driverListSponsorship() {
		final Object testingData[][] = {

			{
				"sponsor1", 1, null
			},//1. All fine 
			{
				"sponsor1", 28, IllegalArgumentException.class
			},//2. Incorrect results
			{
				"manager1", 1, IllegalArgumentException.class
			},//3. Wrong authority

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateListSponsorship((String) testingData[i][0], (Integer) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	protected void templateListSponsorship(final String username, final Integer expectedInt, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			super.authenticate(username);

			final Integer result = this.sponsorshipService.findAllBySponsorId(this.actorService.findByPrincipal().getId()).size();
			Assert.isTrue(expectedInt == result);

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME-CHAMPIONS
	 * a)(Level A) Requirement 43.1: Actors that are authenticated as a sponsor must be able to: Display his/her sponsorships
	 * 
	 * b) Negative cases:
	 * 2.Not training
	 * 3.Null object
	 * 
	 * c) Sentence coverage
	 * -findOne():100%
	 * 
	 * 
	 * d) Data coverage
	 * -Sponsorship = 0%
	 */

	@Test
	public void driverDisplaySponsorship() {
		final Object testingData[][] = {

			{
				"sponsorship1", null
			},//1. All fine
			{
				"player2", IllegalArgumentException.class
			},//2. Not training
			{
				null, IllegalArgumentException.class
			},//3. Null object

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateDisplaySponsorship((String) testingData[i][0], (Class<?>) testingData[i][1]);

	}

	protected void templateDisplaySponsorship(final String sponsorshipId, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			Assert.isTrue(sponsorshipId != null);

			final Sponsorship sponsorship = this.sponsorshipService.findOne(super.getEntityId(sponsorshipId));

			Assert.isTrue(sponsorship != null);

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();

		super.checkExceptions(expected, caught);

	}

	/*
	 * -------Coverage SponsorshipService-------
	 * 
	 * ----TOTAL SENTENCE COVERAGE:
	 * SponsorService = 22,9%
	 * 
	 * ----TOTAL DATA COVERAGE:
	 * sponsorship = 0%
	 */

}
