
package services;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Player;
import domain.StatisticalData;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class StatisticalDataServiceTest extends AbstractTest {

	//The SUT----------------------------------------------------
	@Autowired
	private StatisticalDataService	statisticalDataService;

	@Autowired
	private TeamService				teamService;

	@Autowired
	private PlayerService			playerService;

	@Autowired
	private ManagerService			managerService;


	/*
	 * ----CALCULATE COVERAGE----
	 * The previous delivery, we calculate it manually. In this one instead we are using the plugin called EclEmma,
	 * with which we can automatically calculate the percentage.
	 * 
	 * Each of the test have their result just before them, and the coverage of the complete test is shown at the end of the document.
	 */

	/*
	 * ACME.CHAMPIONS
	 * a)(Level C) Requirement 12.4: An actor who is authenticated as manager must be able to display all player´s data
	 * 
	 * b) Negative cases:
	 * 2.Wrong number of matches
	 * 3.Not player
	 * 
	 * c) Sentence coverage
	 * -findStatisticalDataByPlayerId()=100%
	 * 
	 * d) Data coverage
	 * -StatisticalData: 0%
	 */

	@Test
	public void driverDisplayPlayerData() {
		final Object testingData[][] = {
			{
				"player1", 3, null
			},//1. All fine
			{
				"player1", 7, IllegalArgumentException.class
			},//2. Wrong number of matches
			{
				"president1", 3, NullPointerException.class
			},//3. Not player

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateDisplayPlayerData((String) testingData[i][0], (Integer) testingData[i][1], (Class<?>) testingData[i][2]);
	}

	protected void templateDisplayPlayerData(final String username, final Integer matchesPlayed, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			this.authenticate(username);

			final Player player = this.playerService.findOne(super.getEntityId(username));

			final StatisticalData statisticalData = this.statisticalDataService.findStatisticalDataByPlayerId(player.getId());

			Assert.isTrue(statisticalData.getMatchsPlayed().equals(matchesPlayed));

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * -------Coverage StatisticalDataService-------
	 * 
	 * ----TOTAL SENTENCE COVERAGE:
	 * StatisticalDataService = 22,7%
	 * 
	 * ----TOTAL DATA COVERAGE:
	 * StatisticalData = 0%
	 */

}
