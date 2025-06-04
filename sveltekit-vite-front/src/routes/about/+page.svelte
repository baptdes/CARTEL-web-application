<div class="about-container">
  <h1 class="main-title">Le Cartel, la Bibliothèque ludothèque étudiante de l'ENSEEIHT</h1>

  <section class="about-section">
    <div class="section-header">
      <h2>Mais C'est quoi le Cartel ?</h2>
      <div class="divider"></div>
    </div>
    <p>
      Le Cartel est la Bibliothèque-Ludothèque Étudiante de l'ENSEEIHT. C'est un club où n'importe qui peut venir lire, 
      jouer à des jeux de société, organiser des parties de jeu de rôle ou bien simplement se poser sur le canapé.
    </p>
  </section>

  <section class="about-section"> 
    <div class="section-header">
      <h2>Mais C'est où le Cartel ?</h2>
      <div class="divider"></div>
    </div>
    <p>
      Laissez-moi donc vous raconter une histoire, une histoire qui commence en l'ancienne année de 2010 (vous n'étiez peut-être même pas nés à cette époque haha). À cette époque, le "Cartel" était le nom d'un local, qui ne se trouvait pas à son emplacement actuel. 
      Ce local servait à plusieurs clubs, notamment la bibliothèque, le plus ancien club de l'école, mais aussi Table Ronde, le club de jeux de société, et XIIIème Heure. Ces clubs, bien que partageant les mêmes locaux, étaient bien différents. Mais en cette belle année 2010, 
      pour se simplifier la tâche à tous et pour devenir un club plus important, tous ces clubs ont décidé d'unir leurs forces et de former le Cartel, prenant le nom du local qu'ils partageaient. Ce déménagement ne fut pas sans difficulté, il fallait déplacer plus d'une trentaine d'années de livres, 
      mais nos vaillants membres y arrivèrent. Non sans mal, mais ils y arrivèrent quand même. Pendant plusieurs années, le Cartel continua à grandir dans son nouveau local, au premier étage du bâtiment C. Fort d'un nouveau canapé et d'un monopole total du marché des boissons non alcoolisées à l'ouest de la TR, ils purent moderniser le local. 
      Et c'est ainsi qu'en 2014, le Cartel émigra à nouveau vers son lieu de résidence actuel, le 3ème étage de la Tour Radio.
    </p>
  </section>

  <section class="about-section">
    <div class="section-header">
      <h2>Le local</h2>
      <div class="divider"></div>
    </div>
    <p>
     Avant d'être un club, le Cartel c'est surtout un local, qui est rempli de choses sympas. 
     D'abord une grande quantité de romans, BD, mangas, jeux de société et de livres de règles de jeu de rôle. 
     Mais aussi d'un canapé incroyablement confortable et de deux coins chill, très souvent peuplés par des gens voulant faire une sieste
    entre deux CMs. Le local est normalement ouvert à toutes les pauses et entre midi et deux. 
     Et il peut aussi être ouvert pendant les heures de cours en fonction des disponibilités des membres du bureau.
    </p>
  </section>

  <section class="about-section">
    <div class="section-header">
      <h2>Les événements</h2>
      <div class="divider"></div>
    </div>
    <div class="events-content">
      <p>Le Cartel fait aussi pas mal d'activités au cours de l'année :</p>
      
      <div class="event-item">
        <div class="event-icon">🌞</div>
        <div class="event-description">
          Au printemps et en été, le Cartel se délocalise partiellement en extérieur les midis pour permettre aux gens de jouer à des jeux de société et de profiter du beau temps en même temps.
        </div>
      </div>
      
      <div class="event-item">
        <div class="event-icon">🎲</div>
        <div class="event-description">
          Le Cartel est ouvert presque tous les samedis, de 9h à 18h. Parfois sans activités prévues pour permettre aux gens qui le veulent de venir jouer à des jeux de société plus longs qui ne peuvent pas être terminés en une pause du midi, ou parfois pour des évènements de jeu de rôle. Le Cartel partage aussi son local à d'autres clubs le samedi pour des évènements plus ponctuels, notamment Magic7 et Japan7.
        </div>
      </div>
      
      <div class="event-item">
        <div class="event-icon">🍻</div>
        <div class="event-description">
          Le Cartel organise aussi une semaine de bar, au FOY, avec plein d'évènements comme un tournoi de multigaming + jeux de société, ainsi qu'un afterwork rempli de mystère.
        </div>
      </div>
    </div>
  </section>
</div>

<style>

  
  :global(body) {
    background-color: #2d2d2d;
  }
  .about-container {
    max-width: 1300px;
    margin: 0 auto;
    padding: 2.5rem;
  }

  .main-title {
    font-family: "Pirata One", cursive;
    color: var(--dark-orange);
    font-size: 4.2rem;
    margin-bottom: 3rem;
    text-align: center;
    font-weight: bold;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
    position: relative;
  }

  .main-title::after {
    content: "";
    font-size: 1.5rem;
    position: absolute;
    bottom: -15px;
    left: 50%;
    transform: translateX(-50%);
  }

  .about-section {
    margin-bottom: 3.5rem;
    padding: 1.5rem 0;
    border-left: 4px solid var(--orange);
    padding-left: 1.5rem;
  }

  .section-header {
    position: relative;
    margin-bottom: 1.5rem;
  }

  .divider {
    height: 3px;
    background: linear-gradient(to right, var(--orange), transparent);
    margin-top: 0.5rem;
  }

  h2 {
    font-family: "Pirata One", cursive;
    color: var(--orange);
    font-size: 2.5rem;
    margin: 0;
    font-weight: 600;
    display: inline-block;
  }

  p {
    font-family: "Pirata One", cursive;
    font-size: 1.8rem;
    color: #f5f5f5;
    margin: 0 0 0.8rem 0;
    line-height: 1.7;
  }

  .events-content {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .event-item {
    display: flex;
    gap: 1rem;
    align-items: flex-start;
    padding: 1rem 0;
    border-bottom: 1px dashed var(--orange);
    transition: transform 0.2s;
  }

  .event-item:hover {
    transform: translateX(5px);
  }

  .event-icon {
    font-size: 2rem;
    min-width: 45px;
    height: 45px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--orange);
    color: white;
    border-radius: 50%;
  }

  .event-description {
    font-family: "Pirata One", cursive;
    font-size: 1.6rem;
    color: var(--dark-orange);
    line-height: 1.6;
  }

  @media (max-width: 768px) {
    .about-container {
      padding: 1.5rem;
    }
    
    .main-title {
      font-size: 2.5rem;
    }
    
    h2 {
      font-size: 1.8rem;
    }
    
    p, .event-description {
      font-size: 1.2rem;
    }
    
    .event-item {
      flex-direction: column;
    }
    
    .event-icon {
      margin-bottom: 0.5rem;
    }
  }
</style>