let selectedCompany = '';

function getCompanies(company) {
    let companyElement = document.getElementById(company);
    const xhttp = new XMLHttpRequest();
    selectedCompany = company;

    // Reemplazar espacios por guiones bajos
    let companyFileName = company.replace(/ /g, '_');

    xhttp.onload = function() {
        companiesList = JSON.parse(this.responseText);
        var previous = document.getElementById("previous");
        var next = document.getElementById("next");
        previous.setAttribute('data-page', 0);
        next.setAttribute('data-page', 1);
        displayNextGames("previous");
    }

    xhttp.open("GET", "/" + companyFileName + ".json");
    xhttp.send();
}

function createTable(limit, start) {
    let table = document.getElementById("companyTable").getElementsByTagName('tbody')[0];
    table.innerHTML = '';
    for (let i = start; i < limit; i++) {
        let game = companiesList[i];
        let row = table.insertRow();
        let cell1 = row.insertCell(0);
        let cell2 = row.insertCell(1);
        let cell3 = row.insertCell(2);

        cell1.innerHTML = game.company;
        cell2.innerHTML = game.name;
        cell3.innerHTML = game.consoles;
    }
    changeColor();
}

function displayNextGames(button) {
    var page = parseInt(document.getElementById(button).getAttribute('data-page'));
    var headerText = document.getElementById('text');
    headerText.textContent = selectedCompany + ": Page " + String(page + 1);
    var limit = 0;
    var previous = document.getElementById("previous");
    var next = document.getElementById("next");
    
    if ((page + 1) * 10 > companiesList.length) {
        previous.setAttribute('aria-disabled', false);
        next.setAttribute('aria-disabled', true);
        next.setAttribute('tabindex', -1);
        previous.setAttribute('tabindex', 1);
        previous.setAttribute('data-page', page - 1);
        next.setAttribute('data-page', page);
        limit = companiesList.length;
    } else if (page === 0) {
        previous.setAttribute('aria-disabled', true);
        next.setAttribute('aria-disabled', false);
        previous.setAttribute('tabindex', -1);
        next.setAttribute('tabindex', 1);
        previous.setAttribute('data-page', 0);
        next.setAttribute('data-page', 1);
        limit = 10;
    } else {
        previous.setAttribute('aria-disabled', false);
        next.setAttribute('aria-disabled', false);
        previous.setAttribute('tabindex', 1);
        next.setAttribute('tabindex', 2);
        previous.setAttribute('data-page', page - 1);
        next.setAttribute('data-page', page + 1);
        limit = (page * 10) + 10;
    }
    createTable(limit, page * 10);
}

function changeColor() {
    let companyElement = document.getElementById(selectedCompany);
    if (companyElement) {
        var newColor = companyElement.getAttribute('data-color');
        const tableHeader = document.querySelectorAll('#companyTable thead th');
        tableHeader.forEach(header => header.style.backgroundColor = newColor);
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const companyButtons = document.querySelectorAll('#companies button');
    companyButtons.forEach(button => {
        const color = button.getAttribute('data-color');
        if (color) {
            button.style.backgroundColor = color;
        }
    });
});
