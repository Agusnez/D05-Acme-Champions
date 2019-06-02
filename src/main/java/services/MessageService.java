
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.MessageRepository;
import domain.Actor;
import domain.Box;
import domain.Message;
import forms.MessageForm;

@Service
@Transactional
public class MessageService {

	//Managed Repository

	@Autowired
	private MessageRepository		messageRepository;

	//Supporting services

	@Autowired
	private BoxService				boxService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private Validator				validator;


	//Simple CRUD methods

	public MessageForm create(final int recipientId) {

		Assert.notNull(recipientId);
		Assert.isTrue(recipientId != 0);

		final MessageForm result = new MessageForm();
		Actor recipient;
		Actor sender;

		recipient = this.actorService.findOne(recipientId);
		Assert.notNull(recipient);

		result.setRecipientId(recipient.getId());

		sender = this.actorService.findByPrincipal();
		result.setSenderId(sender.getId());

		return result;

	}

	public MessageForm create2() {

		final MessageForm result = new MessageForm();

		final Actor actor = this.actorService.findByPrincipal();

		result.setSenderId(actor.getId());
		result.setRecipientId(actor.getId());

		return result;

	}

	public Message create3() {

		final Message result = new Message();

		Date momentSent;

		final Actor actor = this.actorService.findByPrincipal();

		momentSent = new Date(System.currentTimeMillis() - 1000);

		final Boolean spam = false;

		result.setSender(actor);
		result.setMoment(momentSent);
		result.setSpam(spam);
		result.setPriority("NEUTRAL");

		return result;

	}

	public Collection<Message> findAll() {

		final Collection<Message> messages = this.messageRepository.findAll();

		Assert.notNull(messages);

		return messages;
	}

	public Message findOne(final int messageId) {

		final Message message = this.messageRepository.findOne(messageId);

		Assert.notNull(message);

		return message;
	}

	public Message save(final Message message) {

		if (message.getId() != 0)
			Assert.isTrue((message.getSender() == this.actorService.findByPrincipal()) || message.getRecipient() == this.actorService.findByPrincipal());

		Assert.notNull(message);

		Message result;

		final Boolean spam1 = this.configurationService.spamContent(message.getSubject());

		final Boolean spam2 = this.configurationService.spamContent(message.getBody());

		final Boolean spam3 = this.configurationService.spamContent(message.getTags());

		final Collection<Box> boxes = message.getBoxes();
		final Box inBoxReceiver = this.boxService.findInBoxByActorId(message.getRecipient().getId());

		if (spam1 || spam2 || spam3) {

			final Box spamBoxReceiver = this.boxService.findSpamBoxByActorId(message.getRecipient().getId());

			boxes.remove(inBoxReceiver);
			boxes.add(spamBoxReceiver);

			message.setBoxes(boxes);

		} else {

		}

		result = this.messageRepository.save(message);

		return result;
	}

	public Message save2(final Message message) {

		Assert.notNull(message);

		Message result;

		result = this.messageRepository.save(message);

		return result;

	}

	public void delete(final Message message) {

		Assert.notNull(message);
		Assert.isTrue(message.getId() != 0);
		final Actor actor = this.actorService.findByPrincipal();
		Assert.notNull(actor);
		//		Assert.isTrue(message.getRecipient().equals(actor) || message.getSender().equals(actor));

		final Box tb = this.boxService.findTrashBoxByActorId(actor.getId());
		final Collection<Box> boxes = message.getBoxes();
		final Collection<Box> allBoxes = this.boxService.findAllBoxByActor(actor.getId());
		if (boxes.contains(tb)) {
			boxes.removeAll(allBoxes);
			message.setBoxes(boxes);
			this.messageRepository.save(message);
			if (boxes.isEmpty())
				this.messageRepository.delete(message);
		} else {
			boxes.removeAll(allBoxes);
			boxes.add(tb);
			message.setBoxes(boxes);
			this.messageRepository.save(message);
			if (boxes.isEmpty())
				this.messageRepository.delete(message);
		}
	}

	public void deleteAll(final int actorId) {

		final Collection<Message> messages = this.messageRepository.AllmessagePerActor(actorId);

		if (!messages.isEmpty())
			for (final Message m : messages)
				this.messageRepository.delete(m);

	}

	public Collection<Message> findMessagesByBoxId(final int boxId) {

		final Collection<Message> result = this.messageRepository.findMessagesByBoxId(boxId);

		return result;

	}

	public Boolean securityMessage(final int boxId) {

		Boolean res = false;

		final Actor ownerBox = this.boxService.findOne(boxId).getActor();

		final Actor login = this.actorService.findByPrincipal();

		if (login.equals(ownerBox))
			res = true;

		return res;
	}

	public Collection<Message> messagePerActor(final int actorId) {

		final Collection<Message> messages = this.messageRepository.messagePerActor(actorId);

		return messages;
	}

	public boolean existId(final int messageId) {
		Boolean res = false;

		final Message message = this.messageRepository.findOne(messageId);

		if (message != null)
			res = true;

		return res;
	}

	public Message reconstruct(final MessageForm message, final BindingResult binding) {

		final Message result = new Message();

		result.setId(message.getId());
		result.setVersion(message.getVersion());
		result.setBody(message.getBody());
		result.setPriority(message.getPriority());
		result.setRecipient(this.actorService.findOne(message.getRecipientId()));
		result.setSender(this.actorService.findOne(message.getSenderId()));
		result.setSpam(false);
		result.setSubject(message.getSubject());
		result.setTags(message.getTags());

		if (message.getId() == 0) {
			final Collection<Box> boxes = new ArrayList<Box>();
			boxes.add(this.boxService.findOutBoxByActorId(message.getSenderId()));
			boxes.add(this.boxService.findInBoxByActorId(message.getRecipientId()));
			result.setBoxes(boxes);

			Date momentSent;
			momentSent = new Date(System.currentTimeMillis() - 1000);
			result.setMoment(momentSent);

		} else {
			final Collection<Box> boxes = this.messageRepository.findOne(message.getId()).getBoxes();
			result.setBoxes(boxes);
			result.setMoment(this.messageRepository.findOne(message.getId()).getMoment());
		}

		final Date momentSent = new Date(System.currentTimeMillis() - 1000);
		result.setMoment(momentSent);

		this.validator.validate(result, binding);

		return result;
	}

	public Collection<Message> AllmessagePerActor(final int actorId) {

		final Collection<Message> result = this.messageRepository.AllmessagePerActor(actorId);

		return result;
	}

	public void flush() {
		this.messageRepository.flush();
	}

}
