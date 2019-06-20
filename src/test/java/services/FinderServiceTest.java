
package services;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Finder;
import domain.President;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class FinderServiceTest extends AbstractTest {

	//The SUT----------------------------------------------------
	@Autowired
	private FinderService		finderService;

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
	 * a)Req27.2: Actors that are authenticated as a president must be able to:
	 * Browse managers and players via finder, hire managers and sign players to join the team.
	 * b) Negative cases:
	 * 2. The number of the players finded is wrong
	 * 3. The actor is not authenticated
	 * c) Sentence coverage
	 * - save(Finder): 84,4%
	 * - findFinderByPresident(int): 100%
	 * d) Data coverage
	 */
	@Test
	public void driverFinder() {
		final Object testingData[][] = {
			{
				"Sara", "president1", 1, null
			},//1. All fine filter
			{
				"", "president1", 1, IllegalArgumentException.class
			},//2. The number of the players finded is wrong
			{
				"", null, 1, IllegalArgumentException.class
			},//3. The actor is not authenticated

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateFinder((String) testingData[i][0], (String) testingData[i][1], (Integer) testingData[i][2], (Class<?>) testingData[i][3]);
	}

	protected void templateFinder(final String keyword, final String username, final Integer results, final Class<?> expected) {
		Class<?> caught;

		caught = null;

		try {
			this.authenticate(username);

			final President president = this.presidentService.findByPrincipal();

			final Finder finder = this.finderService.findFinderByPresident(president.getId());

			finder.setKeyWord(keyword);

			final Finder saved = this.finderService.save(finder);

			Assert.isTrue(saved.getPlayers().size() == results);

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	/*
	 * -------Coverage FinderService-------
	 * 
	 * ----TOTAL SENTENCE COVERAGE:
	 * FinderService = 42%
	 * 
	 * ----TOTAL DATA COVERAGE:
	 * Finder = 0%
	 */

}
