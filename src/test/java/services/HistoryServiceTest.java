
package services;

import java.util.Collection;
import java.util.HashSet;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.History;
import domain.PersonalData;
import domain.Player;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class HistoryServiceTest extends AbstractTest {

	//The SUT----------------------------------------------------
	@Autowired
	private HistoryService		historyService;

	@Autowired
	private PersonalDataService	personalDataService;

	@Autowired
	private PlayerService		playerService;


	/*
	 * ----CALCULATE COVERAGE----
	 * The previous delivery, we calculate it manually. In this one instead we are using the plugin called EclEmma,
	 * with which we can automatically calculate the percentage.
	 * 
	 * Each of the test have their result just before them, and the coverage of the complete test is shown at the end of the document.
	 */

	/*
	 * ACME.CHAMPIONS
	 * a)(Level C) Requirement 13.3: An actor who is authenticated as player must be able to display her/his history.
	 * 
	 * b) Negative cases:
	 * 2. Not history
	 * 3. Null object
	 * 
	 * c) Sentence coverage
	 * -findOne(): 100%
	 * 
	 * d) Data coverage
	 * -History: 0%
	 */

	@Test
	public void driverDisplayHistory() {
		final Object testingData[][] = {

			{
				"history1", null
			},//1. All fine
			{
				"player2", IllegalArgumentException.class
			},//2. Not history
			{
				null, NullPointerException.class
			},//3. Null object

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateDisplayHistory((String) testingData[i][0], (Class<?>) testingData[i][1]);

	}

	protected void templateDisplayHistory(final String historyBean, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			final History history = this.historyService.findOne(super.getEntityId(historyBean));

			Assert.isTrue(history != null);

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();

		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)(Level C) Requirement 13.3: An actor who is authenticated as player must be able to create her/his history.
	 * 
	 * b) Negative cases:
	 * 2. Invalid authority
	 * 3. Null authenticate
	 * 
	 * c) Sentence coverage
	 * -create(): 100%
	 * -save(): 100%
	 * 
	 * d) Data coverage
	 * -History: 0%
	 */

	@Test
	public void driverCreateHistory() {
		final Object testingData[][] = {
			{
				"player4", null
			},//1. All fine
			{
				"manager1", IllegalArgumentException.class
			},//2. Invalid authority
			{
				null, NullPointerException.class
			},//3. Null authenticate
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateCreateHistory((String) testingData[i][0], (Class<?>) testingData[i][1]);
	}
	protected void templateCreateHistory(final String username, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			super.authenticate(username);

			final Player player = this.playerService.findOne(super.getEntityId(username));

			final History h = this.historyService.create();

			final PersonalData pd = this.personalDataService.create();
			final Collection<String> photos = new HashSet<>();
			photos.add("http://photo.com/");
			pd.setPhotos(photos);
			pd.setSocialNetworkProfilelink("http://example.com/");
			this.personalDataService.save(pd);
			this.personalDataService.flush();

			h.setPlayer(player);
			h.setPersonalData(pd);

			this.historyService.save(h);
			this.historyService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.unauthenticate();

		this.rollbackTransaction();

		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)(Level C) Requirement 13.3: An actor who is authenticated as player must be able to delete her/his history.
	 * 
	 * b) Negative cases:
	 * 2. Not history
	 * 3. Invalid authority
	 * 
	 * c) Sentence coverage
	 * -delete(): 100%
	 * -findOne(): 100%
	 * 
	 * d) Data coverage
	 * -History: 0%
	 */

	@Test
	public void driverDeleteHistory() {
		final Object testingData[][] = {

			{
				"player1", "history1", null
			},//1. All fine
			{
				"player1", "player2", IllegalArgumentException.class
			},//2. Not history
			{
				"manager1", "history1", IllegalArgumentException.class
			},//3. Invalid authority

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateDeleteHistory((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	protected void templateDeleteHistory(final String username, final String historyBean, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			this.authenticate(username);

			final History history = this.historyService.findOne(super.getEntityId(historyBean));

			this.historyService.delete(history);

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.unauthenticate();

		this.rollbackTransaction();

		super.checkExceptions(expected, caught);

	}

	/*
	 * -------Coverage HistoryService-------
	 * 
	 * ----TOTAL SENTENCE COVERAGE:
	 * HistoryService = 79,2%
	 * 
	 * ----TOTAL DATA COVERAGE:
	 * History = 28,57143%
	 */
}
