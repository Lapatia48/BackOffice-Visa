<script setup lang="ts">
import { useRouter } from 'vue-router'
import ListEtudiant from '../components/ListEtudiant.vue'

const router = useRouter()

function editEtudiant(etudiant: { id: number }) {
  router.push(`/etudiants/modifier/${etudiant.id}`)
}
</script>

<template>
  <section class="page-card">
    <div class="page-header">
      <div>
        <p class="eyebrow">Étudiants</p>
        <h2>Liste et modification</h2>
      </div>
    </div>

    <ListEtudiant @edit="editEtudiant" />
  </section>
</template>

<style scoped>
.page-card {
  padding: 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.08);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.eyebrow {
  margin: 0 0 4px;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 0.78rem;
}

h2 {
  margin: 0;
}
</style>