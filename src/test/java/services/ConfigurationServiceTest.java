
package services;

import java.util.Collection;
import java.util.HashSet;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Configuration;
import domain.Team;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class ConfigurationServiceTest extends AbstractTest {

	//The SUT--------------------------------------------------
	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private TeamService				teamService;


	/*
	 * ----CALCULATE COVERAGE----
	 * The previous delivery, we calculate it manually. In this one instead we are using the plugin called EclEmma,
	 * with which we can automatically calculate the percentage.
	 * 
	 * Each of the test have their result just before them, and the coverage of the complete test is shown at the end of the document.
	 */

	/*
	 * 
	 * ACME.CHAMPIONS
	 * a)Manage system's configuration.
	 * 
	 * b) Negative cases:
	 * 2. Finder time < 1
	 * 3. Finder time > 100
	 * 
	 * c) Sentence coverage
	 * -findConfiguration(): 100%
	 * -save(): 100%
	 * 
	 * d) Data coverage
	 * -Configuration: 0%
	 */

	@Test
	public void EditConfigurationTest() {
		final Object testingData[][] = {
			{
				"admin", "http://example.com", "+34", "2", "40", "example", "example", "VISA", null
			},//1. All fine
			{
				"admin", "http://example.com", "+34", "-1", "40", "example", "example", "VISA", ConstraintViolationException.class
			},//2. Finder time < 1
			{
				"admin", "http://example.com", "+34", "102", "40", "example", "example", "VISA", ConstraintViolationException.class
			},//3. Finder time > 100

		};

		for (int i = 0; i < testingData.length; i++)
			this.EditConfigurationTemplate((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], Integer.valueOf((String) testingData[i][3]), Integer.valueOf((String) testingData[i][4]), (String) testingData[i][5],
				(String) testingData[i][6], (String) testingData[i][7], (Class<?>) testingData[i][8]);
	}

	protected void EditConfigurationTemplate(final String username, final String banner, final String countryCode, final Integer finderTime, final Integer finderResult, final String welcomeMessage, final String welcomeMessageEs, final String make1,
		final Class<?> expected) {
		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			super.authenticate(username);

			final Configuration config = this.configurationService.findConfiguration();

			config.setBanner(banner);
			config.setCountryCode(countryCode);
			config.setFinderResult(finderResult);
			config.setFinderTime(finderTime);
			config.setWelcomeMessage(welcomeMessage);
			config.setWelcomeMessageEs(welcomeMessageEs);

			final Collection<String> makes = new HashSet<>();
			makes.add(make1);

			config.setMakes(makes);

			this.configurationService.save(config);
			this.configurationService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.unauthenticate();

		this.rollbackTransaction();

		this.checkExceptions(expected, caught);
	}

	/*
	 * ACME.CHAMPIONS
	 * a)(Level B) Requirement 28.7: An actor who is authenticated as a manager must be able to see a prediction of the next matches.
	 * 
	 * b) Negative cases:
	 * 2. Not team
	 * 3. Incorrect number
	 * 
	 * c) Sentence coverage
	 * -goalPrediction(): 81%
	 * 
	 * d) Data coverage
	 * -Configuration: 0%
	 */

	@Test
	public void GoalPredictionTest() {
		final Object testingData[][] = {
			{
				"team1", 1, null
			},//1. All fine
			{
				"manager1", 1, IllegalArgumentException.class
			},//2. Not team
			{
				"team1", 100, IllegalArgumentException.class
			},//3. Incorrect number

		};

		for (int i = 0; i < testingData.length; i++)
			this.goalPredictionTemplate((String) testingData[i][0], (Integer) testingData[i][1], (Class<?>) testingData[i][2]);
	}

	protected void goalPredictionTemplate(final String teamBean, final Integer predictionExpected, final Class<?> expected) {
		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			final Team team = this.teamService.findOne(super.getEntityId(teamBean));

			Assert.isTrue(team != null);

			final Double goals = this.configurationService.goalPrediction(team.getId());

			Assert.isTrue((goals.intValue() == predictionExpected));

			this.configurationService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();

		this.checkExceptions(expected, caught);
	}
	/*
	 * -------Coverage ConfigurationService-------
	 * 
	 * ----TOTAL SENTENCE COVERAGE:
	 * ConfigurationService = 49,4%
	 * 
	 * ----TOTAL DATA COVERAGE:
	 * Configuration = 0%
	 */
}
