
package services;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import utilities.AbstractTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class DashboardServiceTest extends AbstractTest {

	//The SUT--------------------------------------------------
	@Autowired
	private DashboardService	dashboardService;


	/*
	 * ----CALCULATE COVERAGE----
	 * The previous delivery, we calculate it manually. In this one instead we are using the plugin called EclEmma,
	 * with which we can automatically calculate the percentage.
	 * 
	 * Each of the test have their result just before them, and the coverage of the complete test is shown at the end of the document.
	 */

	/*
	 * ACME.CHAMPIONS
	 * a) req 30.3: Actors that are authenticated as administrators must be able to: Have a dashboard
	 * 
	 * b) Negative cases:
	 * 2. Incorrect minOfTrainingsPerManager
	 * 3. Incorrect maxOfTrainingsPerManager
	 * 
	 * c) Sentence coverage
	 * -avgOfTrainingsPerManager(): 100%
	 * -minOfTrainingsPerManager(): 100%
	 * -maxOfTrainingsPerManager(): 100%
	 */

	@Test
	public void DashboardTest() {
		final Object testingData[][] = {
			{
				1.0, 1, 1, null
			},//1. All fine
			{
				1.0, 4, 1, IllegalArgumentException.class
			},//2. Incorrect minOfTrainingsPerManager
			{
				1.0, 1, 10, IllegalArgumentException.class
			},//3. Incorrect maxOfTrainingsPerManager
		};

		for (int i = 0; i < testingData.length; i++)
			this.DashboardTemplate((Double) testingData[i][0], (Integer) testingData[i][1], (Integer) testingData[i][2], (Class<?>) testingData[i][3]);
	}

	protected void DashboardTemplate(final Double avgOfTrainingsPerManagerExpected, final Integer minOfTrainingsPerManagerExpected, final Integer maxOfTrainingsPerManagerExpected, final Class<?> expected) {
		Class<?> caught;

		caught = null;
		try {
			super.authenticate("admin");

			final Double avgOfTrainingsPerManager = this.dashboardService.avgOfTrainingsPerManager();
			final Integer minOfTrainingsPerManager = this.dashboardService.minOfTrainingsPerManager();
			final Integer maxOfTrainingsPerManager = this.dashboardService.maxOfTrainingsPerManager();

			Assert.isTrue(avgOfTrainingsPerManager.equals(avgOfTrainingsPerManagerExpected));
			Assert.isTrue(minOfTrainingsPerManager.equals(minOfTrainingsPerManagerExpected));
			Assert.isTrue(maxOfTrainingsPerManager.equals(maxOfTrainingsPerManagerExpected));

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.unauthenticate();
		this.checkExceptions(expected, caught);
	}
}
