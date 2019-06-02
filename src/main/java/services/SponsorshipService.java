
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

import repositories.SponsorshipRepository;
import security.Authority;
import domain.Actor;
import domain.Game;
import domain.Player;
import domain.Sponsor;
import domain.Sponsorship;
import domain.Team;

@Service
@Transactional
public class SponsorshipService {

	@Autowired
	private SponsorshipRepository	sponsorshipRepository;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private SponsorService			sponsorService;

	@Autowired
	private TeamService				teamService;

	@Autowired
	private GameService				gameService;

	@Autowired
	private PlayerService			playerService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private Validator				validator;


	public Sponsorship createWithTeam(final int teamId) {

		final Sponsor sponsor = this.sponsorService.findByPrincipal();
		Assert.notNull(sponsor);
		final Authority authority = new Authority();
		authority.setAuthority(Authority.SPONSOR);
		Assert.isTrue(sponsor.getUserAccount().getAuthorities().contains(authority));

		final Sponsorship sponsorship = new Sponsorship();

		final Team team = this.teamService.findOne(teamId);

		Assert.isTrue(team != null);
		sponsorship.setCreditCard(sponsor.getCreditCard());
		sponsorship.setSponsor(sponsor);
		sponsorship.setGame(null);
		sponsorship.setPlayer(null);
		sponsorship.setTeam(team);

		return sponsorship;

	}
	public Sponsorship createWithGame(final int gameId) {

		final Sponsor sponsor = this.sponsorService.findByPrincipal();
		Assert.notNull(sponsor);
		final Authority authority = new Authority();
		authority.setAuthority(Authority.SPONSOR);
		Assert.isTrue(sponsor.getUserAccount().getAuthorities().contains(authority));

		final Sponsorship sponsorship = new Sponsorship();

		final Game game = this.gameService.findOne(gameId);
		final Date now = new Date(System.currentTimeMillis() - 1000);

		Assert.isTrue(game != null && game.getGameDate().after(now));

		sponsorship.setCreditCard(sponsor.getCreditCard());
		sponsorship.setSponsor(sponsor);
		sponsorship.setGame(game);
		sponsorship.setPlayer(null);
		sponsorship.setTeam(null);

		return sponsorship;

	}

	public Sponsorship createWithPlayer(final int playerId) {

		final Sponsor sponsor = this.sponsorService.findByPrincipal();
		Assert.notNull(sponsor);
		final Authority authority = new Authority();
		authority.setAuthority(Authority.SPONSOR);
		Assert.isTrue(sponsor.getUserAccount().getAuthorities().contains(authority));

		final Sponsorship sponsorship = new Sponsorship();

		final Player player = this.playerService.findOne(playerId);

		Assert.isTrue(player != null);

		sponsorship.setCreditCard(sponsor.getCreditCard());
		sponsorship.setSponsor(sponsor);
		sponsorship.setGame(null);
		sponsorship.setPlayer(player);
		sponsorship.setTeam(null);

		return sponsorship;

	}

	public Sponsorship findOne(final int sponsorshipId) {

		final Sponsorship sponsorship;
		sponsorship = this.sponsorshipRepository.findOne(sponsorshipId);
		return sponsorship;

	}

	public Collection<Sponsorship> findAll() {

		Collection<Sponsorship> result;
		result = this.sponsorshipRepository.findAll();
		Assert.notNull(result);
		return result;

	}

	public Sponsorship save(final Sponsorship sponsorship) {

		final Sponsor sponsor = this.sponsorService.findByPrincipal();
		Assert.notNull(sponsor);
		final Authority authority = new Authority();
		authority.setAuthority(Authority.SPONSOR);
		Assert.isTrue(sponsor.getUserAccount().getAuthorities().contains(authority));

		Assert.isTrue(sponsorship.getSponsor() == sponsor);

		final Date now = new Date(System.currentTimeMillis() - 1000);

		final String newBanner = this.configurationService.checkURL(sponsorship.getBanner());
		sponsorship.setBanner(newBanner);

		final String newTarjet = this.configurationService.checkURL(sponsorship.getTarget());
		sponsorship.setTarget(newTarjet);

		if (sponsorship.getGame() != null)
			Assert.isTrue(sponsorship.getGame().getGameDate().after(now));

		final Sponsorship result = this.sponsorshipRepository.save(sponsorship);

		return result;

	}
	public Boolean sponsorshipSponsorSecurity(final int sponsorhipId) {
		Boolean res = false;

		final Sponsorship sponsorhip = this.findOne(sponsorhipId);

		final Sponsor login = this.sponsorService.findByPrincipal();

		if (login.equals(sponsorhip.getSponsor()))
			res = true;

		return res;
	}

	public Sponsorship reconstruct(final Sponsorship sponsorship, final BindingResult binding) {
		Sponsorship result = new Sponsorship();

		if (sponsorship.getPlayer() == null && sponsorship.getGame() == null && sponsorship.getTeam() != null) {
			final Team team = this.teamService.findOne(sponsorship.getTeam().getId());

			result = this.createWithTeam(team.getId());

		} else if (sponsorship.getPlayer() != null && sponsorship.getGame() == null && sponsorship.getTeam() == null) {
			final Player player = this.playerService.findOne(sponsorship.getPlayer().getId());

			result = this.createWithPlayer(player.getId());

		} else if (sponsorship.getPlayer() == null && sponsorship.getGame() != null && sponsorship.getTeam() == null) {
			final Game game = this.gameService.findOne(sponsorship.getGame().getId());

			final Date now = new Date(System.currentTimeMillis() - 1000);

			if (game != null && game.getGameDate().after(now))
				result = this.createWithGame(game.getId());

		}

		result.setBanner(sponsorship.getBanner());
		result.setTarget(sponsorship.getTarget());
		this.validator.validate(result, binding);

		return result;
	}

	public void deleteAdmin(final Sponsorship sponsorship) {

		this.sponsorshipRepository.delete(sponsorship);

	}

	//	public void deleteAll(final int actorId) {
	//
	//		final Collection<Sponsorship> sponsorships = this.findAllByProviderId(actorId);
	//
	//		if (!sponsorships.isEmpty())
	//			for (final Sponsorship s : sponsorships)
	//				this.sponsorshipRepository.delete(s);
	//	}

	//	public SponsorshipForm editForm(final Sponsorship sponsorship) {
	//
	//		final SponsorshipForm result = new SponsorshipForm();
	//
	//		result.setBanner(sponsorship.getBanner());
	//		result.setCreditCard(sponsorship.getCreditCard());
	//		result.setId(sponsorship.getId());
	//		result.setPositionId(sponsorship.getPosition().getId());
	//		result.setTarget(sponsorship.getTarget());
	//		result.setVersion(sponsorship.getVersion());
	//
	//		return result;
	//	}

	//	public Sponsorship ramdomSponsorship(final int positionId) {
	//
	//		Sponsorship result = null;
	//		final Collection<Sponsorship> sponsorships = this.sponsorshipRepository.findAllByPositionId(positionId);
	//
	//		final Double vatTax = this.configurationService.findConfiguration().getVatTax();
	//		final Double fare = this.configurationService.findConfiguration().getFare();
	//		if (!sponsorships.isEmpty()) {
	//
	//			final int M = 0;
	//			final int N = sponsorships.size() - 1;
	//			final int limit = (int) (Math.random() * (N - M + 1) + M);
	//
	//			int i = 0;
	//
	//			for (final Sponsorship s : sponsorships) {
	//
	//				if (i == limit) {
	//					result = s;
	//
	//					Double cost = result.getCost();
	//
	//					if (fare != null)
	//						if (vatTax == null)
	//							cost = cost + fare;
	//						else
	//							cost = cost + ((1 + vatTax) * fare);
	//
	//					result.setCost(cost);
	//					break;
	//				}
	//
	//				i++;
	//
	//			}
	//
	//		}
	//
	//		return result;
	//	}

	public Integer findSponsorshipByTeamAndSponsorId(final int teamId, final int sponsorId) {

		return this.sponsorshipRepository.findSponsorshipByTeamAndSponsorId(teamId, sponsorId);

	}

	public Integer findSponsorshipByGameAndSponsorId(final int gameId, final int sponsorId) {

		return this.sponsorshipRepository.findSponsorshipByGameAndSponsorId(gameId, sponsorId);

	}

	public Integer findSponsorshipByPlayerAndSponsorId(final int playerId, final int sponsorId) {

		return this.sponsorshipRepository.findSponsorshipByPlayerAndSponsorId(playerId, sponsorId);

	}

	public Collection<Team> findTeamsAvailableToBeSponsored(final int sponsorId) {

		final Collection<Team> result = this.teamService.findAll();
		final Collection<Sponsorship> sponsorshipsSponsor = this.sponsorshipRepository.findAllBySponsorId(sponsorId);
		for (final Sponsorship s : sponsorshipsSponsor)
			if (s.getTeam() != null && result.contains(s.getTeam()))
				result.remove(s.getTeam());
		return result;

	}

	public Collection<Game> findGamesAvailableToBeSponsored(final int sponsorId) {

		final Date now = new Date(System.currentTimeMillis() - 1000);

		final Collection<Game> result = this.gameService.findAll();
		final Collection<Sponsorship> sponsorshipsSponsor = this.sponsorshipRepository.findAllBySponsorId(sponsorId);

		for (final Sponsorship s : sponsorshipsSponsor)
			if (s.getGame() != null && result.contains(s.getGame()))
				result.remove(s.getGame());

		final Collection<Game> toRemove = new ArrayList<Game>();
		for (final Game g : result)
			if (g.getGameDate().before(now))
				toRemove.add(g);
		result.removeAll(toRemove);

		return result;
	}

	public Collection<Player> findPlayersAvailableToBeSponsored(final int sponsorId) {
		final Collection<Player> result = this.playerService.findAll();
		final Collection<Sponsorship> sponsorshipsSponsor = this.sponsorshipRepository.findAllBySponsorId(sponsorId);
		for (final Sponsorship s : sponsorshipsSponsor)
			if (s.getPlayer() != null && result.contains(s.getPlayer()))
				result.remove(s.getPlayer());
		return result;
	}

	public Collection<Sponsorship> findAllBySponsorId(final int actorId) {

		final Collection<Sponsorship> sponsorships = this.sponsorshipRepository.findAllBySponsorId(actorId);

		return sponsorships;
	}

	public void flush() {

		this.sponsorshipRepository.flush();

	}

	public Collection<Sponsorship> findSponsorshipsByGameId(final int gameId) {

		final Collection<Sponsorship> res = this.sponsorshipRepository.findSponsorshipsByGameId(gameId);

		return res;
	}

	public Collection<Sponsorship> findSponsorshipsByPlayerId(final int playerId) {

		final Collection<Sponsorship> res = this.sponsorshipRepository.findSponsorshipsByPlayerId(playerId);

		return res;
	}

	public Collection<Sponsorship> findSponsorshipsByTeamId(final int teamId) {

		final Collection<Sponsorship> res = this.sponsorshipRepository.findSponsorshipsByTeamId(teamId);

		return res;
	}

	public void deleteForDeleteGame(final Sponsorship sponsorship) {
		final Authority authReferee = new Authority();
		authReferee.setAuthority(Authority.REFEREE);

		//Hay que estar logeado
		final Actor actor = this.actorService.findByPrincipal();
		Assert.notNull(actor);

		//Este caso lo ejecuta un referee
		Assert.isTrue(actor.getUserAccount().getAuthorities().contains(authReferee));

		this.sponsorshipRepository.delete(sponsorship);
	}

}
