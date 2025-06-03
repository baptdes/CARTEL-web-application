<script>
  import { goto } from "$app/navigation";
  import { isAuthenticated, logout } from "$lib/auth";
  import PointBar from "$lib/misc/PointBar.svelte";
  import StackText from "$lib/misc/StackText.svelte";

  let authenticated = $state(false);
  let { children } = $props();

  // $effect(() => {
  //   authenticated = $isAuthenticated;
  //   if (!authenticated) {
  //     goto('/login');
  //   }
  // });

  function handleLogout() {
    logout();
    goto("/login");
  }
</script>

<div class="admin-background">
  <div class="admin-layout">
    <div class="admin-header">
      <button
        type="button"
        onclick={() => {
          goto("/admin");
        }}
      >
        <h1>PANEL<br />ADMIN</h1>
      </button>
      <PointBar
        Color="var(--secondary)"
        startDotSrc="/src/assets/img/icons/star_dual.svg"
        startImageScale={8}
      />
      <button class="logout-button" onclick={handleLogout}>
        <StackText text="Deconnexion" size="2" />
      </button>
    </div>
    <div class="main-wrap">
      {@render children()}
    </div>
  </div>
</div>

<style lang="scss">
  .main-wrap {
    width: 100%;
  }

  .admin-background {
    background-color: var(--back);
    min-height: 100vh;
    width: 100%;
    padding-top: 1px; /* Prevent margin collapse */
  }

  .admin-layout {
    padding: 2rem;
    width: 100%;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
  }

  .admin-header {
    gap: 3rem;
    display: flex;
    justify-content: space-between;
    width: 100%;
    align-items: center;
    margin-bottom: 2rem;
    padding-bottom: 1rem;
    button {
      background-color: var(--back);
      border: none;
      cursor: pointer;
      align-items: center;

      &:hover {
        h1 {
          color: var(--back);
          text-shadow:  0 0 2px var(--accent), 0 0 10px var(--accent);
        }
      }

      h1 {
        font-family: Guisol, monospace;
        color: var(--primary);
        margin: 0;
        text-align: justify;
        transition: all 0.5s;
      }
    }
  }

  :global(.admin-button) {
    background-color: var(--back);
    color: var(--secondary);
    border: 1px solid var(--secondary);
    padding: 0.5rem 1rem;
    border-radius: 0;
    cursor: pointer;
    font-weight: bold;
    transition:
      border 1s,
      color 1s;

    &:hover {
      border: 1px solid var(--accent);
      color: var(--accent);
    }
  }

  .logout-button {
    @extend :global(.admin-button);
  }
</style>
