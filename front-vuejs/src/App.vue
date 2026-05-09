<script setup lang="ts">
function resetApp() {
  window.location.assign('/')
}
</script>

<template>
  <div class="app-shell">
    <header class="app-header">
      <div>
        <h1>BackOffice Visa</h1>
        <p>Recherche structurée des demandes</p>
      </div>
      <button class="reset-btn" @click="resetApp">Réinitialiser</button>
    </header>

    <main class="app-main">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
  padding: 20px;
}

.app-header {
  max-width: 1080px;
  margin: 0 auto 20px;
  padding: 16px 20px;
  border-radius: 14px;
  background: #f1dfc0;
  border: 1px solid #cfb68e;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.app-header h1 {
  margin: 0;
  font-size: 1.5rem;
  color: #4f331e;
}

.app-header p {
  margin: 4px 0 0;
  color: #6a4b31;
}

.app-main {
  max-width: 1080px;
  margin: 0 auto;
}

.reset-btn {
  border: 1px solid #8b6340;
  background: #6f4a2e;
  color: #fff2df;
  border-radius: 10px;
  padding: 9px 12px;
  font-weight: 600;
  cursor: pointer;
}

.reset-btn:hover {
  filter: brightness(1.05);
}

@media (max-width: 700px) {
  .app-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
