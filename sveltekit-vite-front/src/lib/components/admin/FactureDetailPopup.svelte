<script>
  import { createEventDispatcher } from "svelte";

  const dispatch = createEventDispatcher();

  // Props
  const { showPopup = false, facture = null } = $props();

  // Methods
  function closePopup() {
    dispatch("close");
  }

  // Format date for display
  function formatDate(dateString) {
    if (!dateString) return "N/A";
    const date = new Date(dateString);
    return new Intl.DateTimeFormat("fr-FR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    }).format(date);
  }

  // Get item type display name
  function getItemTypeName(item) {
    if (!item) return "Inconnu";

    if (item.format) {
      // Book formats
      if (item.format === "LIVRE") return "Livre";
      if (item.format === "BD") return "Bande Dessinée";
      if (item.format === "MANGA") return "Manga";
      return item.format;
    }

    // Try to guess from item properties
    if (item.authors) return "Livre";
    if (item.minPlayers) return "Jeu";

    return "Article";
  }
</script>

{#if showPopup && facture}
  <div
    class="popup-backdrop"
    role="dialog"
    onclick={closePopup}
    onkeydown={() => {}}
  >
    <div
      class="popup-content facture-detail-popup"
      role="document"
      onclick={(e) => e.stopPropagation()}
      onkeydown={() => {}}
    >
      <h3>Détails de la facture : {facture.filename || "Sans nom"}</h3>

      <div class="facture-detail">
        <div class="facture-info">
          <div class="info-row">
            <span class="info-label">Date:</span>
            {formatDate(facture.updatedAt)}
          </div>
          <div class="info-row">
            <span class="info-label">Nombre d'articles:</span>
            {facture.items ? facture.items.length : 0}
          </div>
        </div>

        <h4>Articles</h4>

        {#if facture.items && facture.items.length > 0}
          <div class="items-detail-container">
            {#each facture.items as item}
              <div class="item-detail-card">
                <div class="item-card-header">
                  <div class="item-image">
                    <img
                      src={item.coverImage || "/placeholder_book.png"}
                      alt={item.name}
                    />
                  </div>
                  <div class="item-header-info">
                    <h4>{item.name || "Sans nom"}</h4>
                    <div class="item-type-badge">{getItemTypeName(item)}</div>
                  </div>
                </div>

                <div class="item-details-grid">
                  <!-- Common information for all items -->
                  <div class="detail-item">
                    <span class="detail-label">Code-barres:</span>
                    <span class="detail-value">{item.barcode || "N/A"}</span>
                  </div>
                  <div class="detail-item">
                    <span class="detail-label">Année de publication:</span>
                    <span class="detail-value"
                      >{item.publicationYear || "N/A"}</span
                    >
                  </div>
                  <div class="detail-item">
                    <span class="detail-label">Langue:</span>
                    <span class="detail-value">{item.language || "N/A"}</span>
                  </div>

                  <!-- Book-specific information -->
                  {#if item.format && (item.format === "LIVRE" || item.format === "BD" || item.format === "MANGA")}
                    <div class="detail-item">
                      <span class="detail-label">Format:</span>
                      <span class="detail-value">{item.format}</span>
                    </div>
                    {#if item.authors && item.authors.length}
                      <div class="detail-item">
                        <span class="detail-label">Auteur(s):</span>
                        <span class="detail-value">
                          {item.authors
                            .map(
                              (a) => `${a.firstname || ""} ${a.surname || ""}`
                            )
                            .join(", ")}
                        </span>
                      </div>
                    {/if}
                    {#if item.publisher}
                      <div class="detail-item">
                        <span class="detail-label">Éditeur:</span>
                        <span class="detail-value"
                          >{item.publisher.name || "N/A"}</span
                        >
                      </div>
                    {/if}
                    {#if item.series}
                      <div class="detail-item">
                        <span class="detail-label">Série:</span>
                        <span class="detail-value"
                          >{item.series.name || "N/A"}</span
                        >
                      </div>
                    {/if}
                    {#if item.volumeNumber}
                      <div class="detail-item">
                        <span class="detail-label">Tome:</span>
                        <span class="detail-value">{item.volumeNumber}</span>
                      </div>
                    {/if}

                    <!-- Game-specific information -->
                  {:else if item.minPlayers || item.maxPlayers}
                    {#if item.minPlayers && item.maxPlayers}
                      <div class="detail-item">
                        <span class="detail-label">Joueurs:</span>
                        <span class="detail-value">
                          {item.minPlayers === item.maxPlayers
                            ? `${item.minPlayers} joueur${item.minPlayers > 1 ? "s" : ""}`
                            : `${item.minPlayers} - ${item.maxPlayers} joueurs`}
                        </span>
                      </div>
                    {/if}
                    {#if item.avgPlaytime}
                      <div class="detail-item">
                        <span class="detail-label">Durée:</span>
                        <span class="detail-value">{item.avgPlaytime} min</span>
                      </div>
                    {/if}
                    {#if item.creator}
                      <div class="detail-item">
                        <span class="detail-label">Créateur:</span>
                        <span class="detail-value">
                          {item.creator.firstname}
                          {item.creator.surname}
                        </span>
                      </div>
                    {/if}
                    {#if item.categories && item.categories.length}
                      <div class="detail-item">
                        <span class="detail-label">Catégories:</span>
                        <span class="detail-value"
                          >{item.categories.join(", ")}</span
                        >
                      </div>
                    {/if}
                  {/if}
                </div>

                {#if item.description}
                  <div class="item-description">
                    <span class="description-label">Description:</span>
                    <p>{item.description}</p>
                  </div>
                {/if}
              </div>
            {/each}
          </div>
        {:else}
          <div class="no-items">Cette facture ne contient aucun article.</div>
        {/if}
      </div>

      <div class="popup-actions">
        <button
          type="button"
          class="close-btn"
          onclick={closePopup}
        >
          Fermer
        </button>
      </div>
    </div>
  </div>
{/if}

<style lang="scss">
  .popup-backdrop {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.7);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
  }

  .popup-content {
    background-color: var(--back);
    color: var(--primary);
    border: 2px solid var(--accent);
    border-radius: 0px;
    padding: 2rem;
    width: 90%;
    max-width: 800px;
    max-height: 90vh;
    overflow-y: auto;
    position: relative;

    h3 {
      margin-top: 0;
      text-align: center;
      margin-bottom: 1.5rem;
      color: var(--back);
      -webkit-text-stroke: 1px var(--accent);
      text-transform: uppercase;
      font-size: 2.5rem;
      font-family: Guisol;
      transform: scaleY(1.5);
    }
  }

  .facture-detail {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;

    .facture-info {
      background-color: rgba(0, 0, 0, 0.05);
      border-radius: 4px;
      padding: 1rem;

      .info-row {
        margin-bottom: 0.5rem;

        &:last-child {
          margin-bottom: 0;
        }

        .info-label {
          font-weight: 600;
          margin-right: 0.5rem;
        }
      }
    }

    h4 {
      color: var(--primary);
      margin: 0.5rem 0;
      font-size: 1.2rem;
    }
  }

  .facture-detail-popup {
    width: 95%;
    max-width: 900px;
  }

  .items-detail-container {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .item-detail-card {
    background: rgba(255, 255, 255, 0.03);
    border: 1px solid var(--secondary);
    border-radius: 8px;
    padding: 1.2rem;
    display: flex;
    flex-direction: column;
    gap: 1rem;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);

    .item-card-header {
      display: flex;
      gap: 1rem;

      .item-image {
        width: 80px;
        min-width: 80px;
        height: 120px;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          border-radius: 4px;
          box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
        }
      }

      .item-header-info {
        display: flex;
        flex-direction: column;
        justify-content: center;

        h4 {
          margin: 0;
          margin-bottom: 0.5rem;
          font-size: 1.2rem;
          color: var(--primary);
        }

        .item-type-badge {
          display: inline-block;
          background-color: var(--accent);
          color: white;
          padding: 0.3rem 0.8rem;
          font-size: 0.8rem;
          border-radius: 20px;
          font-weight: 600;
          align-self: flex-start;
        }
      }
    }

    .item-details-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
      gap: 0.8rem;
      border-top: 1px solid rgba(0, 0, 0, 0.1);
      border-bottom: 1px solid rgba(0, 0, 0, 0.1);
      padding: 1rem 0;

      .detail-item {
        display: flex;
        flex-direction: column;

        .detail-label {
          font-size: 0.8rem;
          color: var(--secondary);
          margin-bottom: 0.2rem;
        }

        .detail-value {
          font-weight: 500;
        }
      }
    }

    .item-description {
      .description-label {
        font-size: 0.8rem;
        color: var(--secondary);
        margin-bottom: 0.4rem;
        display: block;
      }

      p {
        margin: 0;
        line-height: 1.5;
        font-size: 0.95rem;
        color: var(--primary);
      }
    }
  }

  .no-items {
    text-align: center;
    padding: 1.5rem;
    color: var(--secondary);
    background-color: rgba(0, 0, 0, 0.05);
    border-radius: 4px;
  }

  .popup-actions {
    display: flex;
    justify-content: center;
    margin-top: 2rem;

    .close-btn {
      padding: 0.8rem 2rem;
      border-radius: 4px;
      background: var(--accent);
      color: white;
      border: none;
      font-weight: 600;
      cursor: pointer;

      &:hover {
        filter: brightness(0.9);
      }
    }
  }
</style>