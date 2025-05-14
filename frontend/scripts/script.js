const formulario = document.querySelector("form");

const Inome = document.querySelector(".nome");
const Iemail = document.querySelector(".email");
const Isenha = document.querySelector(".senha");
const Itel = document.querySelector(".tel");

const mensagemDiv = document.createElement("div");
mensagemDiv.className = "mensagem";
formulario.prepend(mensagemDiv); // Insere antes do formulário

function mostrarMensagem(texto, tipo) {
    mensagemDiv.textContent = texto;
    mensagemDiv.className = "mensagem " + tipo; // Classes CSS: 'sucesso' ou 'erro'
}

function cadastrar(){

     if (!Inome.value || !Iemail.value || !Isenha.value || !Itel.value) {
        mostrarMensagem("Preencha todos os campos!", "erro");
        return;
    }

    fetch("http://localhost:8080/usuarios",
        {
            headers: {
                'Accept': 'application/json',
                'Content-type': 'application/json'
            },
            method: "POST",
            body: JSON.stringify({ 
                nome: Inome.value,
                email: Iemail.value,
                senha: Isenha.value,
                telefone: Itel.value
               })
        })
    .then(response => {
        if (response.ok) {
            mostrarMensagem("Usuário cadastrado com sucesso!", "sucesso");
            limpar();
        } else {
            response.json().then(data => {
                mostrarMensagem(data.mensagem || "Erro ao cadastrar", "erro");
            });
        }
    })
    .catch(error => {
        mostrarMensagem("Erro na conexão com o servidor", "erro");
    });
};



function limpar (){
    Inome.value = "";
    Iemail.value = "";
    Isenha.value = "";
    Itel.value = "";
};

formulario.addEventListener("submit", function(event){
event.preventDefault();

cadastrar();
limpar();

});