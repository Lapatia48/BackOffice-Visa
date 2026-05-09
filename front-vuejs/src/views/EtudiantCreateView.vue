<script setup lang="ts">
import { useRouter } from 'vue-router'
import Etudiant from '../components/Etudiant.vue'

const router = useRouter()

function created() {
  router.push('/etudiants')
}
</script>

<template>
  <section class="page-card">
    <p class="eyebrow">Étudiants</p>
    <h2>Créer un étudiant</h2>
    <Etudiant @created="created" />
  </section>
</template>

<style scoped>
.page-card {
  padding: 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.08);
}

.eyebrow {
  margin: 0 0 4px;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 0.78rem;
}

h2 {
  margin: 0 0 16px;
}
</style>