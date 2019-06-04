
package services;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.FinderRepository;
import security.Authority;
import domain.Actor;
import domain.Finder;
import domain.Manager;
import domain.Player;
import domain.President;

@Service
@Transactional
public class FinderService {

	// Managed Repository ------------------------
	@Autowired
	private FinderRepository		finderRepository;

	// Supporting services ------------------------

	@Autowired
	private ActorService			actorService;

	@Autowired
	private Validator				validator;

	@Autowired
	private ManagerService			managerService;

	@Autowired
	private PlayerService			playerService;

	@Autowired
	private PresidentService		presidentService;

	@Autowired
	private ConfigurationService	configurationService;


	public Finder create() {

		Finder result;

		result = new Finder();

		result.setLastUpdate(new Date(System.currentTimeMillis() - 1000));

		return result;

	}

	public Collection<Finder> findAll() {

		final Collection<Finder> result = this.finderRepository.findAll();
		Assert.notNull(result);
		return result;

	}

	public Finder findOne(final int finderId) {

		final Finder result = this.finderRepository.findOne(finderId);
		return result;

	}

	public Finder save(final Finder finder) {
		Assert.notNull(finder);

		if (finder.getId() != 0) {
			final Actor actor = this.actorService.findByPrincipal();
			Assert.notNull(actor);

			final Authority hW = new Authority();
			hW.setAuthority(Authority.PRESIDENT);
			if (actor.getUserAccount().getAuthorities().contains(hW)) {
				final President owner = finder.getPresident();
				Assert.notNull(owner);
				Assert.isTrue(actor.getId() == owner.getId());
				finder.setLastUpdate(new Date(System.currentTimeMillis() - 1000));
				Collection<Manager> managerSearchedList = new HashSet<Manager>();
				if (finder.getPosition() == null || finder.getPosition() == "")
					managerSearchedList = this.managerService.findManagersByFinder(finder);
				finder.setManagers(managerSearchedList);
				final Collection<Player> playerSearchedList = this.playerService.findPlayersByFinder(finder);
				finder.setPlayers(playerSearchedList);
			}
		} else
			finder.setLastUpdate(new Date(System.currentTimeMillis() - 1000));

		final Finder result = this.finderRepository.save(finder);

		return result;

	}

	public Finder saveAdmin(final Finder finder) {

		final Finder result = this.finderRepository.save(finder);

		return result;
	}

	public void deleteManagersPlayers(final Finder finder) {
		final Collection<Manager> managers = new HashSet<Manager>();
		final Collection<Player> players = new HashSet<Player>();
		finder.setManagers(managers);
		finder.setPlayers(players);
		finder.setKeyWord("");
		finder.setPosition("");
	}

	//	public void deleteFinderActor(final int actorId) {
	//
	//		final Finder finder = this.finderRepository.findFinderByRookie(actorId);
	//
	//		this.finderRepository.delete(finder);
	//
	//	}

	// Other business rules

	public Finder reconstruct(final Finder finder, final BindingResult binding) {

		final Finder finderBBDD = this.findOne(finder.getId());

		finder.setPresident(finderBBDD.getPresident());

		finder.setLastUpdate(new Date(System.currentTimeMillis() - 1000));

		this.validator.validate(finder, binding);

		return finder;

	}

	public Finder findFinderByPresident(final int presidentId) {
		final Finder finder = this.finderRepository.findFinderByPresident(presidentId);
		return finder;
	}
	//
	//	public Collection<Finder> findFindersByPositionId(final int positionId) {
	//
	//		final Collection<Finder> result = this.finderRepository.findFindersByPositionId(positionId);
	//
	//		return result;
	//	}

	public Boolean security(final int finderId) {
		Boolean res = false;
		Assert.notNull(finderId);

		final President presidentNow = this.presidentService.findByPrincipal();

		final Finder finder = this.findOne(finderId);

		final President owner = finder.getPresident();

		if (presidentNow.getId() == owner.getId())
			res = true;

		return res;
	}

	public Boolean checkInputs(final Finder finder) {
		final Finder finderBBDD = this.findOne(finder.getId());
		Boolean areDifferents = false;

		final Boolean key = finder.getKeyWord().equals(finderBBDD.getKeyWord());
		final Boolean pos = finder.getPosition().equals(finderBBDD.getPosition());

		final Date currentTime = new Date(System.currentTimeMillis() - 1000);
		final Interval interval = new Interval(finderBBDD.getLastUpdate().getTime(), currentTime.getTime());
		final Integer timeOut = this.configurationService.findConfiguration().getFinderTime();

		if (!key || !pos || interval.toDuration().getStandardHours() > timeOut)
			areDifferents = true;

		return areDifferents;
	}

}
