
package services;

import java.util.Collection;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Federation;
import domain.Format;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class FormatServiceTest extends AbstractTest {

	//The SUT----------------------------------------------------
	@Autowired
	private FormatService		formatService;

	@Autowired
	private FederationService	federationService;


	/*
	 * a)Req33.3: Actors that are authenticated as a federation must be able to:
	 * Manage their formats: Create
	 * b) Negative cases:
	 * 2. The min of the property minimumTeams must be 2
	 * 3. The type of format is TOURNAMENT or LEAGUE
	 * c) Sentence coverage
	 * - create(): 100%
	 * - findAll(): 100%
	 * - save(Format): 100%
	 * 
	 * d) Data coverage
	 */
	@Test
	public void driverCreateFormat() {
		final Object testingData[][] = {
			{
				"federation1", "TOURNAMENT", 2, 4, null
			},//1. All fine filter
			{
				"federation1", "LEAGUE", 1, 2, ConstraintViolationException.class
			},//2. The min of the property minimumTeams must be 2
			{
				"federation1", "otro", 2, 2, ConstraintViolationException.class
			},//3. The type of format is TOURNAMENT or LEAGUE

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateCreateFormat((String) testingData[i][0], (String) testingData[i][1], (Integer) testingData[i][2], (Integer) testingData[i][3], (Class<?>) testingData[i][4]);
	}

	protected void templateCreateFormat(final String username, final String type, final Integer min, final Integer max, final Class<?> expected) {
		Class<?> caught;

		caught = null;

		try {
			this.authenticate(username);

			final Federation federation = this.federationService.findByPrincipal();

			final Format format = this.formatService.create();

			format.setFederation(federation);
			format.setType(type);
			format.setMinimumTeams(min);
			format.setMaximumTeams(max);

			final Format saved = this.formatService.save(format);

			final Collection<Format> formats = this.formatService.findAll();

			Assert.isTrue(formats.contains(saved));

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	/*
	 * a)Req33.3: Actors that are authenticated as a federation must be able to:
	 * Manage their formats: Edit
	 * b) Negative cases:
	 * 2. The type of format is TOURNAMENT or LEAGUE
	 * 3. The property type is null
	 * c) Sentence coverage
	 * - save(Format): 100%
	 * d) Data coverage
	 */
	@Test
	public void driverEditFormat() {
		final Object testingData[][] = {
			{
				"federation1", "format2", "TOURNAMENT", 2, 4, null
			},//1. All fine filter
			{
				"federation1", "format2", "otro", 2, 2, ConstraintViolationException.class
			},//2. The type of format is TOURNAMENT or LEAGUE
			{
				"federation1", "format2", null, 2, 2, ConstraintViolationException.class
			},//3. The property type is null

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateEditFormat((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Integer) testingData[i][3], (Integer) testingData[i][4], (Class<?>) testingData[i][5]);
	}

	protected void templateEditFormat(final String username, final String format, final String type, final Integer min, final Integer max, final Class<?> expected) {
		Class<?> caught;

		caught = null;

		try {
			this.authenticate(username);

			final Format formatFind = this.formatService.findOne(super.getEntityId(format));

			formatFind.setType(type);
			formatFind.setMinimumTeams(min);
			formatFind.setMaximumTeams(max);

			final Format saved = this.formatService.save(formatFind);

			final Collection<Format> formats = this.formatService.findAll();

			Assert.isTrue(formats.contains(saved));

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	/*
	 * a)Req33.3: Actors that are authenticated as a federation must be able to:
	 * Manage their formats: List
	 * b) Negative cases:
	 * 2. The number of format finded is wrong
	 * 3. The query hasn't found any format
	 * c) Sentence coverage
	 * - findFormatByFederationId(int): 100%
	 * d) Data coverage
	 */
	@Test
	public void driverListFormat() {
		final Object testingData[][] = {
			{
				"federation1", 2, null
			},//1. All fine filter
			{
				"federation1", 3, IllegalArgumentException.class
			},//2. The number of format finded is wrong
			{
				"federation1", null, NullPointerException.class
			},//3. The query hasn't found any format

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateListFormat((String) testingData[i][0], (Integer) testingData[i][1], (Class<?>) testingData[i][2]);
	}

	protected void templateListFormat(final String username, final Integer countFinded, final Class<?> expected) {
		Class<?> caught;

		caught = null;

		try {
			this.authenticate(username);

			final Collection<Format> formats = this.formatService.findFormatByFederationId(super.getEntityId(username));

			Assert.isTrue(formats.size() == countFinded);

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	/*
	 * a)Req33.3: Actors that are authenticated as a federation must be able to:
	 * Manage their formats: Delete
	 * b) Negative cases:
	 * 2. The actor is not the owner
	 * 3. The format is null
	 * c) Sentence coverage
	 * - delete(Format): 97,2%
	 * d) Data coverage
	 */
	@Test
	public void driverDeleteFormat() {
		final Object testingData[][] = {
			{
				"federation1", "format1", null
			},//1. All fine filter
			{
				"president1", "format1", IllegalArgumentException.class
			},//2. The actor is not the owner
			{
				"president1", null, IllegalArgumentException.class
			},//3. The format is null

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateDeleteFormat((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
	}

	protected void templateDeleteFormat(final String username, final String format, final Class<?> expected) {
		Class<?> caught;

		caught = null;

		try {
			this.authenticate(username);

			Assert.isTrue(format != null);

			final Format formatFind = this.formatService.findOne(super.getEntityId(format));

			this.formatService.delete(formatFind);

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	/*
	 * a)Req33.3: Actors that are authenticated as a federation must be able to:
	 * Manage their formats: Display
	 * b) Negative cases:
	 * 2. Not format
	 * 3. Null object
	 * c) Sentence coverage
	 * - findOne(): 100%
	 * d) Data coverage
	 */
	@Test
	public void driverDisplayFormat() {
		final Object testingData[][] = {
			{
				"federation1", "format1", null
			},//1. All fine filter
			{
				"federation1", "manager1", IllegalArgumentException.class
			},//2. Not format
			{
				"federation1", null, IllegalArgumentException.class
			},//3. Null object

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateListFormat((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
	}

	protected void templateListFormat(final String username, final String format, final Class<?> expected) {
		Class<?> caught;

		caught = null;

		try {
			this.authenticate(username);

			Assert.isTrue(format != null);

			final Format formatFind = this.formatService.findOne(super.getEntityId(format));

			Assert.isTrue(formatFind != null);

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	/*
	 * -------Coverage FormatService-------
	 * 
	 * ----TOTAL SENTENCE COVERAGE:
	 * FormatService = 56,5%
	 * 
	 * ----TOTAL DATA COVERAGE:
	 * Format = 0%
	 */
}
