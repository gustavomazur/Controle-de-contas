const DEFAULT_API = "http://localhost:8000";

const state = {
  apiBase: localStorage.getItem("apiBase") || DEFAULT_API,
};

const $ = (id) => document.getElementById(id);

const money = (n) =>
  (Number(n) || 0).toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });

function toast(message, type = "info") {
  const el = $("toast");
  el.textContent = message;
  el.className = `toast show ${type}`;
  clearTimeout(toast._t);
  toast._t = setTimeout(() => {
    el.className = "toast";
  }, 3000);
}

async function api(path, options = {}) {
  const res = await fetch(`${state.apiBase}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });
  if (!res.ok) {
    let detail = `Erro ${res.status}`;
    try {
      const body = await res.json();
      if (body.detail) detail = JSON.stringify(body.detail);
    } catch {
      /* resposta sem JSON */
    }
    throw new Error(detail);
  }
  if (res.status === 204) return null;
  return res.json();
}

/* ---------------- Navegação (menu sanduíche) ---------------- */

function openDrawer() {
  $("drawer").classList.add("open");
  $("overlay").classList.add("show");
  $("menu-toggle").setAttribute("aria-expanded", "true");
}

function closeDrawer() {
  $("drawer").classList.remove("open");
  $("overlay").classList.remove("show");
  $("menu-toggle").setAttribute("aria-expanded", "false");
}

function goTo(page) {
  document.querySelectorAll(".page").forEach((section) => {
    section.classList.toggle("active", section.id === `page-${page}`);
  });
  document.querySelectorAll(".nav-item").forEach((item) => {
    item.classList.toggle("active", item.dataset.page === page);
  });
  const active = document.querySelector(`.nav-item[data-page="${page}"]`);
  if (active) $("page-title").textContent = active.dataset.title;
  closeDrawer();
  window.scrollTo({ top: 0 });
}

/* ---------------- Tabelas ---------------- */

function renderTable(tbodyId, items, emptyText, onSave, onDelete) {
  const tbody = $(tbodyId);
  tbody.innerHTML = "";
  if (!items || items.length === 0) {
    const tr = document.createElement("tr");
    tr.className = "empty-row";
    const td = document.createElement("td");
    td.colSpan = 4;
    td.textContent = emptyText;
    tr.appendChild(td);
    tbody.appendChild(tr);
    return;
  }
  for (const item of items) {
    tbody.appendChild(makeRow(item, onSave, onDelete));
  }
}

function makeRow(item, onSave, onDelete) {
  const tr = document.createElement("tr");
  showViewRow(tr, item, onSave, onDelete);
  return tr;
}

function showViewRow(tr, item, onSave, onDelete) {
  tr.innerHTML = "";

  const idTd = document.createElement("td");
  idTd.className = "col-id";
  idTd.textContent = `#${item.id}`;

  const titleTd = document.createElement("td");
  titleTd.textContent = item.title;

  const valorTd = document.createElement("td");
  valorTd.className = "col-valor";
  valorTd.textContent = money(item.valor);

  const actionsTd = document.createElement("td");
  actionsTd.className = "col-actions";
  const box = document.createElement("div");
  box.className = "actions-cell";
  const editBtn = document.createElement("button");
  editBtn.className = "mini-btn";
  editBtn.textContent = "Editar";
  editBtn.addEventListener("click", () => showEditRow(tr, item, onSave, onDelete));

  const deleteBtn = document.createElement("button");
  deleteBtn.className = "mini-btn danger";
  deleteBtn.textContent = "Excluir";
  deleteBtn.addEventListener("click", () => {
    if (confirm(`Excluir "${item.title}"?`)) onDelete(item);
  });

  box.append(editBtn, deleteBtn);
  actionsTd.appendChild(box);

  tr.append(idTd, titleTd, valorTd, actionsTd);
}

function showEditRow(tr, item, onSave, onDelete) {
  tr.innerHTML = "";

  const idTd = document.createElement("td");
  idTd.className = "col-id";
  idTd.textContent = `#${item.id}`;

  const titleTd = document.createElement("td");
  const titleInput = document.createElement("input");
  titleInput.type = "text";
  titleInput.className = "edit-input";
  titleInput.value = item.title;
  titleTd.appendChild(titleInput);

  const valorTd = document.createElement("td");
  valorTd.className = "col-valor";
  const valorInput = document.createElement("input");
  valorInput.type = "number";
  valorInput.step = "0.01";
  valorInput.min = "0";
  valorInput.className = "edit-input";
  valorInput.value = item.valor;
  valorTd.appendChild(valorInput);

  const actionsTd = document.createElement("td");
  actionsTd.className = "col-actions";
  const box = document.createElement("div");
  box.className = "actions-cell";

  const saveBtn = document.createElement("button");
  saveBtn.className = "mini-btn primary";
  saveBtn.textContent = "Salvar";

  const cancelBtn = document.createElement("button");
  cancelBtn.className = "mini-btn";
  cancelBtn.textContent = "Cancelar";

  async function commit() {
    const title = titleInput.value.trim();
    const valor = Number(valorInput.value);
    if (!title || Number.isNaN(valor)) {
      toast("Preencha título e valor.", "error");
      return;
    }
    try {
      await onSave(item, { title, valor });
      showViewRow(tr, { ...item, title, valor }, onSave, onDelete);
    } catch (err) {
      toast(`Erro ao salvar: ${err.message}`, "error");
    }
  }

  saveBtn.addEventListener("click", commit);
  cancelBtn.addEventListener("click", () => showViewRow(tr, item, onSave, onDelete));
  for (const input of [titleInput, valorInput]) {
    input.addEventListener("keydown", (e) => {
      if (e.key === "Enter") commit();
      if (e.key === "Escape") showViewRow(tr, item, onSave, onDelete);
    });
  }

  box.append(saveBtn, cancelBtn);
  actionsTd.appendChild(box);
  tr.append(idTd, titleTd, valorTd, actionsTd);
  titleInput.focus();
  titleInput.select();
}

/* ---------------- Dados ---------------- */

async function carregarSaldo() {
  const saldo = await api("/salario/saldo");
  $("total-salario").textContent = money(saldo.total_salario);
  $("total-divida").textContent = money(saldo.total_divida);
  const el = $("saldo");
  el.textContent = money(saldo.saldo);
  el.className = `value ${saldo.saldo >= 0 ? "positive" : "negative"}`;
}

async function carregarDividas() {
  const data = await api("/divida/");
  renderTable("tbody-dividas", data.dividas, "Nenhuma dívida cadastrada.", salvarDivida, excluirDivida);
}

async function carregarSalarios() {
  const data = await api("/salario/");
  renderTable("tbody-salarios", data.salario, "Nenhum salário cadastrado.", salvarSalario, excluirSalario);
}

async function salvarDivida(item, dados) {
  await api(`/divida/${item.id}`, {
    method: "PUT",
    body: JSON.stringify(dados),
  });
  toast("Dívida atualizada.", "success");
  await carregarSaldo();
}

async function salvarSalario(item, dados) {
  await api(`/salario/${item.id}`, {
    method: "PUT",
    body: JSON.stringify(dados),
  });
  toast("Salário atualizado.", "success");
  await carregarSaldo();
}

async function excluirDivida(item) {
  try {
    await api(`/divida/${item.id}`, { method: "DELETE" });
    toast("Dívida excluída.", "success");
    await recarregar();
  } catch (err) {
    toast(`Erro ao excluir: ${err.message}`, "error");
  }
}

async function excluirSalario(item) {
  try {
    await api(`/salario/${item.id}`, { method: "DELETE" });
    toast("Salário excluído.", "success");
    await recarregar();
  } catch (err) {
    toast(`Erro ao excluir: ${err.message}`, "error");
  }
}

async function recarregar() {
  try {
    await Promise.all([carregarSaldo(), carregarDividas(), carregarSalarios()]);
  } catch (err) {
    toast(`Falha ao falar com a API: ${err.message}`, "error");
  }
}

function bindForm(formId, endpoint, label) {
  $(formId).addEventListener("submit", async (event) => {
    event.preventDefault();
    const form = event.target;
    const title = form.title.value.trim();
    const valor = Number(form.valor.value);
    if (!title || Number.isNaN(valor)) return;
    try {
      await api(endpoint, {
        method: "POST",
        body: JSON.stringify({ title, valor }),
      });
      form.reset();
      toast(`${label} adicionada.`, "success");
      await recarregar();
    } catch (err) {
      toast(`Erro ao adicionar: ${err.message}`, "error");
    }
  });
}

/* ---------------- Tema (claro / escuro) ---------------- */

function applyTheme(theme) {
  document.documentElement.setAttribute("data-theme", theme);
  localStorage.setItem("theme", theme);
  const btn = $("theme-toggle");
  if (btn) btn.textContent = theme === "light" ? "Modo escuro" : "Modo claro";
}

function initTheme() {
  const saved = localStorage.getItem("theme") || "dark";
  applyTheme(saved);
  $("theme-toggle").addEventListener("click", () => {
    const current = document.documentElement.getAttribute("data-theme");
    applyTheme(current === "light" ? "dark" : "light");
  });
}

/* ---------------- Init ---------------- */

function init() {
  initTheme();

  $("menu-toggle").addEventListener("click", () => {
    $("drawer").classList.contains("open") ? closeDrawer() : openDrawer();
  });
  $("overlay").addEventListener("click", closeDrawer);

  document.querySelectorAll(".nav-item").forEach((item) => {
    item.addEventListener("click", () => goTo(item.dataset.page));
  });
  document.querySelectorAll("[data-goto]").forEach((btn) => {
    btn.addEventListener("click", () => goTo(btn.dataset.goto));
  });

  $("refresh").addEventListener("click", recarregar);

  bindForm("form-divida", "/divida/", "Dívida");
  bindForm("form-salario", "/salario/", "Salário");

  recarregar();
}

document.addEventListener("DOMContentLoaded", init);
