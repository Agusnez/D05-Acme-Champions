
package services;

import java.util.Collection;
import java.util.HashSet;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import utilities.AbstractTest;
import domain.PersonalData;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class PersonalDataServiceTest extends AbstractTest {

	//The SUT----------------------------------------------------
	@Autowired
	private PersonalDataService	personalDataService;


	/*
	 * ----CALCULATE COVERAGE----
	 * The previous delivery, we calculate it manually. In this one instead we are using the plugin called EclEmma,
	 * with which we can automatically calculate the percentage.
	 * 
	 * Each of the test have their result just before them, and the coverage of the complete test is shown at the end of the document.
	 */

	/*
	 * ACME.CHAMPIONS
	 * a)Requirement 29.3: An actor who is authenticated as a player must be able to create her/his personal data.
	 * 
	 * b) Negative cases:
	 * 2. Photo not url
	 * 3. SocialNetworkProfilelink = not url
	 * 
	 * c) Sentence coverage
	 * -create(): 100%
	 * -save(): 100%
	 * 
	 * d) Data coverage
	 * -PersonalData: 50%
	 */

	@Test
	public void driverCreatePersonalData() {
		final Object testingData[][] = {
			{
				"player1", "http://test.com/", "http://test.com/", null
			},//1. All fine
			{
				"player1", "test", "http://test.com/", DataIntegrityViolationException.class
			},//2. Photo not url
			{
				"player1", "http://test.com/", "test", ConstraintViolationException.class
			},//3. SocialNetworkProfilelink = not url
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateCreatePersonalData((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);
	}

	protected void templateCreatePersonalData(final String username, final String photo, final String socialNetworkProfilelink, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			super.authenticate(username);

			final PersonalData data = this.personalDataService.create();

			final Collection<String> photos = new HashSet<>();
			photos.add(photo);
			data.setPhotos(photos);
			data.setSocialNetworkProfilelink(socialNetworkProfilelink);

			this.personalDataService.save(data);
			this.personalDataService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.unauthenticate();

		this.rollbackTransaction();

		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)Requirement 29.3: An actor who is authenticated as a player must be able to edit her/his personal data.
	 * 
	 * b) Negative cases:
	 * 2. SocialNetworkProfilelink = not url
	 * 3. SocialNetworkProfilelink = Blank
	 * 4. SocialNetworkProfilelink = null
	 * 
	 * c) Sentence coverage
	 * -findOne(): 100%
	 * -save(): 100%
	 * 
	 * d) Data coverage
	 * -PersonalData: 50%
	 */

	@Test
	public void driverEditPersonalData() {
		final Object testingData[][] = {
			{
				"personalData1", "http://test.com/", "http://test.com/", null
			},//1. All fine
			{
				"personalData1", "http://test.com/", "test", ConstraintViolationException.class
			},//2. SocialNetworkProfilelink = not url
			{
				"personalData1", "http://test.com/", "		", ConstraintViolationException.class
			},//3. SocialNetworkProfilelink = Blank
			{
				"personalData1", "http://test.com/", null, ConstraintViolationException.class
			},//4. SocialNetworkProfilelink = null
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateEditPersonalData((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);
	}

	protected void templateEditPersonalData(final String bean, final String photo, final String socialNetworkProfilelink, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			final PersonalData data = this.personalDataService.findOne(super.getEntityId(bean));

			final Collection<String> photos = new HashSet<>();
			photos.add(photo);
			data.setPhotos(photos);
			data.setSocialNetworkProfilelink(socialNetworkProfilelink);

			this.personalDataService.save(data);
			this.personalDataService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();

		super.checkExceptions(expected, caught);

	}

	/*
	 * -------Coverage PersonalDataService-------
	 * 
	 * ----TOTAL SENTENCE COVERAGE:
	 * PersonalDataService = 40%
	 * 
	 * ----TOTAL DATA COVERAGE:
	 * PersonalData = 100%
	 */
}
