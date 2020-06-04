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

function checkValueFood(value,arr){
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
let selectedFoodItem = $("#foodName");
selectedFoodItem.change(()=>{
    if(checkValueFood(selectedFoodItem.val(),plats1)=='Exist')
    {
        $("#food").val("15000");
    }else if (checkValueFood(selectedFoodItem.val(),plats2)=='Exist'){
        $("#food").val("10000");
    }else if (checkValueFood(selectedFoodItem.val(),plats3)=='Exist'){
        $("#food").val("12000");
    }else if (checkValueFood(selectedFoodItem.val(),plats4)=='Exist'){
        $("#food").val("20000");
    }else if (checkValueFood(selectedFoodItem.val(),plats5)=='Exist'){
        $("#food").val("5");
    }
    else if (checkValueFood(selectedFoodItem.val(),plats6)=='Exist'){
        $("#food").val("5000");
    }else if (checkValueFood(selectedFoodItem.val(),plats7)=='Exist'){
        $("#food").val("20");
    }else if (checkValueFood(selectedFoodItem.val(),plats8)=='Exist'){
        $("#food").val("4000");
    }
});