
package services;

import java.util.ArrayList;
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
import domain.Box;
import domain.Message;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class MessageServiceTest extends AbstractTest {

	//The SUT----------------------------------------------------
	@Autowired
	private MessageService	messageService;

	@Autowired
	private ActorService	actorService;

	@Autowired
	private BoxService		boxService;


	/*
	 * ----CALCULATE COVERAGE----
	 * The previous delivery, we calculate it manually. In this one instead we are using the plugin called EclEmma,
	 * with which we can automatically calculate the percentage.
	 * 
	 * Each of the test have their result just before them, and the coverage of the complete test is shown at the end of the document.
	 */

	/*
	 * ACME-CHAMPIONS
	 * a)Requirement 26.3 : Actors can exchange messages
	 * 
	 * b)Negative cases:
	 * 2. Body blank
	 * 3. Subject blank
	 * 
	 * c) Sentence coverage:
	 * -create3()=100%
	 * -save()=59.5%
	 * -findMessagesByBox()=100%
	 * 
	 * 
	 * d) Data coverage:
	 */
	@Test
	public void driverExchangeMessage() {
		final Object testingData[][] = {
			{
				"federation1", "president1", "Body1", "Subject1", "Tag1", null
			},//1.All right
			{
				"federation1", "president1", "", "Subject1", "Tag1", ConstraintViolationException.class
			},//2.Body blank
			{
				"federation1", "president1", "Body1", "", "Tag1", ConstraintViolationException.class
			},//3.Subject blank

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateExchangeMessage((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Class<?>) testingData[i][5]);
	}
	protected void templateExchangeMessage(final String sender, final String recipient, final String body, final String subject, final String tags, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {
			this.startTransaction();
			if (sender != null)
				super.authenticate(sender);

			final Integer senderId = this.actorService.findByPrincipal().getId();
			final Integer recipientId = this.actorService.findOne(super.getEntityId(recipient)).getId();

			final Message message = this.messageService.create3();
			message.setBody(body);
			message.setRecipient(this.actorService.findOne(super.getEntityId(recipient)));
			message.setSubject(subject);
			message.setTags(tags);
			final Collection<Box> boxes = new ArrayList<>();

			final Box outboxsender = this.boxService.findOutBoxByActorId(senderId);
			final Box inboxrecipient = this.boxService.findInBoxByActorId(recipientId);

			boxes.add(outboxsender);
			boxes.add(inboxrecipient);
			message.setBoxes(boxes);

			final Message saved = this.messageService.save(message);
			this.messageService.flush();

			final Box recipientBox = this.boxService.findInBoxByActorId(super.getEntityId(recipient));

			final Collection<Message> messages = this.messageService.findMessagesByBoxId(recipientBox.getId());
			Assert.isTrue(messages.contains(saved));

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		if (sender != null)
			super.unauthenticate();
		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * -------Coverage MessageService-------
	 * 
	 * ----TOTAL SENTENCE COVERAGE:
	 * 
	 * MessageService: 16,3%
	 * 
	 * ----TOTAL DATA COVERAGE:
	 */
}
