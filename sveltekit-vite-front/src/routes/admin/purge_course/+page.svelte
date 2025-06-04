<script>
  import { goto } from "$app/navigation";
  import { adminPageState } from "../store.js";
  import { isAuthenticated, logout } from "$lib/auth";
  import DoubleText from "$lib/misc/DoubleText.svelte";
  import StackText from "$lib/misc/StackText.svelte";
  import PointBar from "$lib/misc/PointBar.svelte";
  import Starline from "$lib/misc/Starline.svelte";
  import { onMount } from "svelte";
  import {
    fetchAllSuggestions,
    deleteSuggestion,
  } from "$lib/services/suggestionService.js";

  // Add this function to apply random rotations
  onMount(() => {
    const rows = document.querySelectorAll(".zigzag tbody tr");

    rows.forEach((row) => {
      // Generate random rotation between -2 and 2 degrees
      const randomRotation = ((Math.random() - 1) * 0.5).toFixed(2);
      row.style.transform = `rotate(${randomRotation}deg)`;
    });
  });

  let suggestions = [
    {
      name: "Exemple",
      type: "Jeu de saucisse",
      description: "Moi quand je me cascade le sheet",
      createdAt: "Hier",
    },
    {
      name: "Exemple 2",
      type: "Jeu de saucisse",
      description: "Moi quand je me cascade le sheet",
      createdAt: "Hier",
    },
    {
      name: "Exemple 3",
      type: "Jeu de saucisse",
      description: "Moi quand je me cascade le sheet",
      createdAt: "Hier",
    },
  ];
  let error = "";

  onMount(async () => {
    await loadSuggestions();
  });

  async function loadSuggestions() {
    try {
      let fetched = await fetchAllSuggestions();
      const typeOrder = ["LIVRE", "BD", "MANGA", "JDS", "JDR", "AUTRE"];
      fetched.sort((a, b) => {
        const idxA = typeOrder.indexOf(a.type?.toUpperCase() || "AUTRE");
        const idxB = typeOrder.indexOf(b.type?.toUpperCase() || "AUTRE");
        return idxA - idxB;
      });
      suggestions = fetched;
    } catch (e) {
      error = "Erreur lors du chargement des suggestions.";
    }
    scrollTo(0, 0);
  }

  async function handleDelete(id) {
    if (confirm("Êtes-vous sûr de vouloir supprimer cette suggestion ?")) {
      try {
        await deleteSuggestion(id);
        await loadSuggestions();
      } catch (e) {
        error = "Erreur lors de la suppression de la suggestion.";
      }
    }
  }

  // PDF export using jsPDF and autotable
  async function exportPDF() {
    const { jsPDF } = await import("jspdf");
    const autoTable = (await import("jspdf-autotable")).default;
    const doc = new jsPDF();
    autoTable(doc, {
      head: [["Nom", "Type", "Description", "Date"]],
      body: suggestions.map((s) => [
        s.name,
        s.type,
        s.description,
        s.createdAt?.slice(0, 10) || "",
      ]),
    });
    doc.save("suggestions.pdf");
  }
</script>

<main>
  <div class="export-buttons">
    <button on:click={exportPDF}>📄 Exporter PDF</button>
  </div>
  <DoubleText text="Purge" size="4em" />
  <!-- <button class="return-button" type="button" onclick={() => { $adminPageState = 0; goto('/admin'); }}>Retour</button> -->

  <PointBar Color="var(--accent)" width="70%" />

  {#if error}
    <div class="error">
      <DoubleText text={error} />
    </div>
  {/if}

  {#if suggestions.length > 0}
    <table class="zigzag">
      <thead>
        <tr>
          <th>
            <span>Nom</span>
          </th>
          <th>
            <span>Type</span>
          </th>
          <th>
            <span>Description</span>
          </th>
          <th>
            <span>Date</span>
          </th>
          <th>
            <span>Action</span>
          </th>
        </tr>
      </thead>
      <tbody>
        {#each suggestions as s}
          <tr>
            <td>{s.name}</td>
            <td>{s.type}</td>
            <td>{s.description}</td>
            <td>{s.createdAt?.slice(0, 10)}</td>
            <td>
              <button on:click={() => handleDelete(s.id)}> Supprimer </button>
            </td>
          </tr>
        {/each}
      </tbody>
    </table>
  {:else}
    <p>Aucune suggestion trouvée.</p>
  {/if}

  <div style="margin-bottom: 4rem;"></div>

  <PointBar Color="var(--accent)" width="90%" />

  <div style="margin-bottom: 3rem;"></div>

  <Starline
    content={[
      { type: "icon*", content: "/src/assets/img/icons/arc_ring.svg" },
      { type: "line", content: "1%" },
      { type: "text", content: "the" },
      { type: "line", content: "50%" },
      { type: "text*", content: "legends" },
      { type: "line", content: "60%" },
      { type: "icon", content: "/src/assets/img/icons/star_hexa.svg" },
    ]}
  />

  <Starline
    content={[
      { type: "icon", content: "/src/assets/img/icons/earth_flat.svg" },
      { type: "line", content: "30%" },
      { type: "text*", content: "speak" },
      { type: "line", content: "30%" },
      { type: "text", content: "of" },
      { type: "line*", content: "10%" },
      { type: "text", content: "a" },
      { type: "line", content: "10%" },
      { type: "icon*", content: "/src/assets/img/icons/warp_tall.svg" },
    ]}
  />

  <Starline
    content={[
      { type: "icon", content: "/src/assets/img/icons/star_area.svg" },
      { type: "line*", content: "5%" },
      { type: "text", content: "cleansing" },
      { type: "line", content: "40%" },
      { type: "text*", content: "fire" },
      { type: "line", content: "30%" },
      { type: "icon", content: "/src/assets/img/icons/circle_grid_flat.svg" },
    ]}
  />

  <img
    src="/src/assets/img/thres/deinos.jpg"
    alt="Warp Skew"
    class=""
    style="width: 100%; height: auto; margin-top: 2em; border: 1px solid var(--primary);"
  />
</main>

<style lang="scss">
  @use "/src/lib/sass/base" as base;

  .export-buttons {
    display: flex;
    gap: 1em;
    position: absolute;
    right: 2em;
    top: 2em;
    z-index: 10;
  }
  .export-buttons button {
    background: var(--secondary);
    color: var(--back);
    border: none;
    padding: 0.5em 1.2em;
    border-radius: 4px;
    cursor: pointer;
    font-weight: bold;
    transition:
      background 0.5s ease-in-out,
      color 0.5s ease-in-out;
  }
  .export-buttons button:hover {
    background: var(--accent);
    color: var(--primary);
  }

  main {
    flex: auto;
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
  }

  .zigzag {
    border-collapse: separate;
    border-spacing: 0.25em 1em;
  }

  .zigzag tbody tr {
    transition: transform 10s ease-in-out;
  }

  table {
    margin-top: 2rem;
    border-collapse: collapse;
    width: 90%;
    max-width: 900px;
    background: transparent;
    color: #000;
    border: 3px solid var(--back);
  }

  thead {
    border: 1px solid var(--back);
    font-family: "Guisol";
    text-align: center;
    tr {
      text-align: center;
      th {
        text-align: center;
        span {
          font-size: 2rem;
          text-align: center;
          text-transform: uppercase;
        }
      }
    }
  }

  th,
  td {
    // border: 1px dotted #ddd;
    padding: 0.7rem 1rem;
    text-align: left;
    color: #000;
    background: var(--back);
    border-radius: 10px;
  }
  th {
    background: var(--accent);
    color: var(--back);
  }

  td {
    color: var(--primary);
  }

  tr {
    background: var(--back);
    color: var(--primary);
  }

  .error {
    color: var(--accent);
    margin: 1rem 0;
  }
  button {
    background: var(--tertiary);
    color: #fff;
    border: none;
    padding: 0.4em 1em;
    border-radius: 4px;
    cursor: pointer;
    transition: background 0.5s;
  }
  button:hover {
    background: var(--accent);
  }
</style>
