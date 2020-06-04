var plats1 = [
    "Poison Fumé + Fufu",
    "Thomson grier au Makala",
    "Poison Bitoyo",
]
var plats2 = [
    "1/4 Poulet Acomp Frites",
    "Mitshop ya Ntaba",
]
var plats3 = [
    "Poison Salé + Banane ou Frites",
    "Ndakala + Fufu",
    "Sausuce + Frite",
]
var plats4 = [
    "Jeune Capitaine + Banane",
    "Tilapia Frets + Frites ou Chikuange",
]
var plats5 = [
    "Samussa 6 Pieces",
    "Croque Madame Pain Oeuf Lait Salade",
    "Tacos Boeuf",
    "Crep Sucre",
    "Crep Sallé",
    "Salade Mixte",
    "Salade Nichoise",
    "Salade de Fruit",
    "Dejeune Complet",
    "Cossa AcmpFrites",
]
var plats6 = [
    "Dejeune Simple",
    "Hambourger Simple",
]
var plats7 = [
    "Pizza Bolonaise Viande",
    "Pizza Mare - Fruit de Mere",
    "Pizza Mexicana-Saucise",
    "Pizza 4 Fraumage Viande/Poulet",
    "Pizza 4 Saison",
    "Plache Miche Samoussa: 1/4 de Poulet Frites Sauces",
]

var plats8 = [
    "Frites Simple",
    "Banane Simple",
    "Sandwich",
    "Brochette de Capitaine",
    "Chawarma Poulet",
]
var smallBeers = [
    "MUTZIG pt",
    "MUTZIG class pt",
    "BEAUFORT Pt",
    "PRIMUS Pt",
    "TURBO King pt",
    "LEGENDE pt",
]
var bigBeers1 = [
    "PRIMUS Gd",
    "SKOL Gd",
    "VICTOIRE",
    "NKOYI Gd",
    "NKOYI Rumba",
    "TEMBO",
    "33 EXPORT",
    "TURBO King",
]
var bigBeers2 = [
    "CASTEL Gd",
    "HEINEKEN Btlle",
    "MUTZIG Gd",
    "MUTZIG class Gd",
    "BEAUFORT Gd",
    "DOPPEL",
    "MUTZIG Bock",
]

var sucree1 = [
    "Fanta petit",
    "XXL",
    "Fayrouz",
    "Coca-cola",
    "Coca-cola zero",
    "Top",
]
var sucree2 = [
    "Malta",
    "Matina",
    "Tonic",
    "Schweppes soda",
    "Vital'o petit",
    "Sprite",
]
var eaux = [
    "Eau plate ou eau vive Gd",
    "Eau plate ou eau vive Pt",
]
var whisky = [
    "J&B",
    "Jhonnie W, Red label",
    "VODKA ABSOLUT",
]

var whisky2 = [
    "Drostdy Hof grand vin par 24",
    "Nederburg WMAS Baronne par 24",
    "Chemin de pape",
    "Baron de Madrid",
    "Bailly",
]

var whisky3 =[
    "Cires",
    "Don Simon",
    "Sangria",
    "Jhonnie W, Black label mesure",
    "Jack Daniels mesure",
    "VODKA GREY ROOSE mesure",
]

function checkValue(value,arr){
    var status = 'Not exist';

    for(var i=0; i<arr.length; i++){
        var name = arr[i];
        if(name == value){
            status = 'Exist';
            break;
        }
    }

    return status;
}

// article automatical changing prices
let selectedBoissonItem = $("#boissonName");
selectedBoissonItem.change(() => {
    if(checkValue(selectedBoissonItem.val(),smallBeers)=='Exist')
    {

        $("#prixU").val("2500");
    }else if (checkValue(selectedBoissonItem.val(),bigBeers1)=='Exist')
    {
        $("#prixU").val("3500");
    }else if (checkValue(selectedBoissonItem.val(),bigBeers2)=='Exist')
    {
        $("#prixU").val("3000");
    }else if (checkValue(selectedBoissonItem.val(),sucree1)=='Exist')
    {
        $("#prixU").val("2000");
    }else if (checkValue(selectedBoissonItem.val(),sucree2)=='Exist')
    {
        $("#prixU").val("1500");
    }else if (checkValue(selectedBoissonItem.val(),eaux)=='Exist')
    {
        $("#prixU").val("1000");
    }else if (checkValue(selectedBoissonItem.val(),whisky)=='Exist')
    {
        $("#prixU").val("25");
    }else if (checkValue(selectedBoissonItem.val(),whisky2)=='Exist')
    {
        $("#prixU").val("30");
    }else if (checkValue(selectedBoissonItem.val(),whisky3)=='Exist'){
        $("#prixU").val("8000");
    }
});




let selectedFoodItem = $("#foodName");
selectedFoodItem.change(()=>{
    if(checkValue(selectedFoodItem.val(),plats1)=='Exist')
    {
        $("#food").val("15000");
    }else if (checkValue(selectedFoodItem.val(),plats2)=='Exist'){
        $("#food").val("10000");
    }else if (checkValue(selectedFoodItem.val(),plats3)=='Exist'){
        $("#food").val("12000");
    }else if (checkValue(selectedFoodItem.val(),plats4)=='Exist'){
        $("#food").val("20000");
    }else if (checkValue(selectedFoodItem.val(),plats5)=='Exist'){
        $("#food").val("5");
    }
    else if (checkValue(selectedFoodItem.val(),plats6)=='Exist'){
        $("#food").val("5000");
    }else if (checkValue(selectedFoodItem.val(),plats7)=='Exist'){
        $("#food").val("20");
    }else if (checkValue(selectedFoodItem.val(),plats8)=='Exist'){
        $("#food").val("4000");
    }
});
