<script>
  import { createEventDispatcher } from 'svelte';
  
  // Props
  let { loan, isEmpruntMode } = $props();
  
  // Events
  const dispatch = createEventDispatcher();
  
  // Format date for display
  function formatDate(dateStr) {
    if (!dateStr) return "N/A";
    const date = new Date(dateStr);
    return new Intl.DateTimeFormat("fr-FR").format(date);
  }
  
  // Complete loan handler
  function handleComplete() {
    dispatch('complete', { id: loan.id });
  }
  
  // Delete loan handler
  function handleDelete() {
    dispatch('delete', { id: loan.id });
  }
</script>

<div class="item">
  <div class="itemInfo">
    <img
      src={loan.itemCoverImage || "/placeholder.jpg"}
      class="itemImage"
      alt={"Image de " + (loan.itemName || "")}
    />
    <div class="itemDetails">   
      <p class="type">
        {#if isEmpruntMode}
          Emprunt
        {:else}
          Prêt
        {/if}
        —
        {#if loan.endDate}
          Terminé
        {:else}
          En cours
        {/if}
      </p>
      <p class="title">{loan.itemName || "Sans titre"}</p>
      <p class="details">
        {#if isEmpruntMode}
          Emprunté par <strong>{loan.borrowerName}</strong>
        {:else}
          Prêté par <strong>{loan.ownerName}</strong>
        {/if}
        <br />
        {#if isEmpruntMode}
          Contact : <strong>{loan.borrowerContact || "N/A"}</strong>
        {:else}
          Contact : <strong>{loan.ownerContact || "N/A"}</strong>
        {/if}
        <br />
        {#if isEmpruntMode}
            Date d'emprunt:
        {:else}
            Date de prêt:
        {/if} <strong>{formatDate(loan.loanDate)}</strong>
        {#if loan.endDate}
          <br />
          Date de retour: <strong>{formatDate(loan.endDate)}</strong>
        {/if}
      </p>
    </div>
  </div>

  <div class="itemActions">
    {#if !loan.endDate}
    <button class="loanInfo" onclick={handleComplete}>
            Terminer
        <img src="/src/assets/img/icons/star_rf.svg" alt="cross" />
    </button>
    {/if}
    <button class="delete-button" onclick={handleDelete}>
        Supprimer
    </button>
  </div>
</div>

<style lang="scss">
  .item {
    display: flex;
    flex-grow: 1;
    gap: 0.5em;
    min-height: 160px;
    width: 100%;
    flex-direction: row;
    border: 1px solid var(--primary);
    padding: 0.5em;
  }

  .itemInfo {
    display: flex;
    flex-direction: row;
    gap: 1em;
    flex-grow: 3;

    .itemImage {
      aspect-ratio: 1;
      object-fit: cover;
      max-height: 150px;
    }

    .itemDetails {
      display: flex;
      flex-direction: column;
      gap: 0.1em;
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

        strong {
          color: var(--primary);
        }
      }
    }
  }

  .loanInfo {
    display: flex;
    align-items: center;
    justify-content: center;
    color:var(--white);
    cursor: pointer;
    border: 1px solid var(--primary);
    text-transform: uppercase;
    font-family: inherit;
    font-weight: bold;
    height: 50px;
    width: auto;
    border-radius: 0;

    img {
        margin-left: 1rem;
      height: 100%;
      object-fit: contain;
      rotate: 45deg;
      transition: rotate 1s ease-out;
    }

    &:hover {
      img {
        rotate: 315deg;
      }
    }
  }

  .delete-button {
    color: var(--accent);
    border: 1px solid var(--accent);
    border-radius: 0;
  }

  .itemActions {
    display: flex;
    flex-direction: column;
    align-items: end;
    justify-content:space-between;
    padding: 1rem 0.5rem;
  }
</style>
