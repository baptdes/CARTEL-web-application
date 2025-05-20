<script>
  import { goto } from "$app/navigation";
  import { adminPageState } from "../store.js";
  import { isAuthenticated, logout } from "$lib/auth";
  import DoubleText from "$lib/misc/DoubleText.svelte";
  import PointBar from "$lib/misc/PointBar.svelte";

  const exampleItems = [
    {
      borrower: "Marc Pantel",
      loanDate: "2023-10-01",
      item: {
        id: 3,
        title: "Catan",
        author: "Klaus Teuber",
        description:
          "Jeu de stratégie et de développement où les joueurs doivent coloniser une île en construisant des villes et des routes.",
        coverImage:
          "https://www.espritjeu.com/upload/image/catan-p-image-65490-grande.jpg",
        publicationYear: 1995,
        category: "Jeu de stratégie",
        available: false,
        type: "boardgame",
        publisher: "Kosmos",
        minPlayers: 3,
        maxPlayers: 4,
        playTime: "60-120 min",
        minAge: 10,
      },
    },
  ];
</script>

<main>
  <DoubleText text="Emprunts/Prêts" size="4em" />
  <!-- <button class="return-button" type="button" onclick={() => { $adminPageState = 0; goto('/admin'); }}>Retour</button> -->

  <PointBar Color="var(--accent)" width="70%" />

  <br style="margin-bottom: 1em;" />

  <div class="search-bar">
    <input type="text" placeholder="Rechercher un emprunt/prêt..." />
    <button class="admin-button" type="button">Rechercher</button>
  </div>

  <br style="margin-bottom: 2em;" />

  <div class="results">
    {#each exampleItems as loan}
      <div class="item">
        <div class="itemInfo">
          <img
            src={loan.item.coverImage || "/placeholder_game.png"}
            class="itemImage"
            alt="Image de {loan.item.title}"
          />
          <div class="itemDetails">
            <p class="type">{loan.item.type} — {loan.item.category}</p>
            <p class="title">{loan.item.title}</p>
            <p class="details">{loan.item.description}</p>
          </div>
        </div>

        <div class="loanInfo">
          <p>
            Emprunté par <em>{loan.borrower}</em><br />le
            <em>{loan.loanDate}</em>
          </p>
        </div>

        <button class="cross">
          <img src="/src/assets/img/icons/star_rf.svg" alt="cross" />
        </button>
      </div>
    {/each}
  </div>
</main>

<style lang="scss">
  @use "/src/lib/sass/base";

  main {
    flex: auto;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  input {
    @extend .admin-button;
    width: 100%;
    min-height: 50px;
    padding: 0.2em;
    font-family: Guisol;
    font-size: 1.5em;
    text-transform: uppercase;
  }

  .search-bar {
    width: 90%;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 1rem;
  }

  .item {
    display: flex;
    flex-grow: 1;
    gap: 0.5em;
    height: 160px;
    width: 100%;
    flex-direction: row;
    border: 1px solid var(--primary);
    padding: 0.5em;
  }

  .itemInfo {
    display: flex;
    flex-direction: row;
    gap: 1em;

    .itemImage {
      // width: 25%;
      aspect-ratio: 1;
      object-fit: cover;
    }

    .itemDetails {
      display: flex;
      flex-direction: column;
      gap: 0.1em;
      // width: 75%;
      font-size: 1.2em;
      align-content: space-between;
      margin-top: 0.5em;
      margin-bottom: 0.5em;
      margin-right: 0.5em;

      .type {
        color: var(--accent);
        font-size: 0.8em;
        text-transform: uppercase;
      }

      .title {
        font-weight: bold;
        font-size: 1.5em;
        color: var(--primary);
      }

      .details {
        min-width: 0px;
        font-size: 0.9em;
        color: var(--secondary);
        height: 100%;
        word-wrap: break-word;
      }
    }
  }

  .loanInfo {
    border: 1px solid var(--accent);
    padding: 0.5em;
    flex-grow: 1;
    color: var(--accent);
    align-content: center;
    text-align: center;
    p {
      em {
        text-transform: uppercase;
        font-weight: bold;
        color: var(--primary);
      }
    }
  }

  .cross {
    width: 10%;
    aspect-ratio: 1;
    flex-shrink: 0;
    position: relative;
    background-color: var(--back);
    padding: 0%;
    border: 1px solid var(--accent);
    border-radius: 0%;

    img {
      width: 100%;
      height: 100%;
      object-fit: contain;
      rotate: 45deg;
      transition: rotate 1s ease-out;
    }
  }

  .cross:hover {
    img {
      rotate: 315deg;
    }
  }
</style>
