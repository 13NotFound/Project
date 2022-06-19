const enc = new TextEncoder("utf-8");
function convertArrayBufferToBase64(arrayBuffer) {
    return btoa(String.fromCharCode(...new Uint8Array(arrayBuffer)));
}

document.addEventListener('DOMContentLoaded', () => {
    // connect to local db
    let request = indexedDB.open("myDatabase",1);
    request.onerror = function (event){
        console.error("An error occured with IndexedDB");
        console.error(event);
    };
    request.onsuccess = function (event) {

        const db = request.result;
        request = db.transaction('user','readonly').objectStore('user').getAll();
        request.onsuccess = () =>{
            console.log("17",request.result)
            for (let index in request.result){
                console.log(21,request.result[index].user_name)
                if (request.result[index].user_name !== username){

                    const div = document.createElement('div');

                    let btn = document.createElement("button");
                    btn.id = request.result[index].user_name;
                    btn.class = "btn btn-warning"
                    btn.type = "button"
                    btn.innerText =
                    btn.onclick = function (){
                        document.getElementById('friend_name').value = btn.id
                        document.getElementById('friend').submit()
                    }
                    btn.innerHTML = "  " +   "Start chat with: " + "<b>" + "  " + request.result[index].user_name +  "  " +"</b>";
                    div.append(btn)

                    document.querySelector('#display-friends-section').append(div);

                }
            }
        }
    }
    var socket = io.connect(location.protocol + '//' + document.domain + ':' + location.port);


    socket.on('friend-name-not-exist', data => {
        alert("Name " + data.friend_name + " not exist.")
    });

    socket.on('request-confirmed', data => {
        alert("Added " + data.friend_name + " as your friend.")
        location.reload()
        storeFriendKeyinStorage(data.friend_name,JSON.parse(data.friend_public_key),JSON.parse(data.friend_public_sign_key))
    });

    document.querySelector('#send_friend_request').onclick = () =>{
        socket.emit('friend-request',{friend_name: document.querySelector('#friend_request').value})
    }

    function storeFriendKeyinStorage(friend_name, public_key, public_sign_key){
        const current_user = {
            user_name: friend_name,
            PbK:public_key,
            PrK:0,
            SPbK: public_sign_key,
            SPrK:0
        }
        addUser(current_user)
    }

    function addUser(user){
        let request = indexedDB.open("myDatabase",1);
        request.onerror = function (event){
            console.error("An error occured with IndexedDB");
            console.error(event);
        };
        request.onsuccess = function (event) {

            const transaction = request.result.transaction('user', 'readwrite');

            const objectStore = transaction.objectStore('user');

            let add_request = objectStore.add(user);

            add_request.onsuccess = () => {
                // request.result contains key of the added object
                console.log(`New user infor added`, user);
            }

            add_request.onerror = (err) => {
                console.error(`Error to add new user: ${err}`)
            }
        }

    }
    async function importSecretKey(jwKey) {
        return window.crypto.subtle.importKey(
            "jwk",
            jwKey,
            {name:"RSA-OAEP", hash:"SHA-256"},
            true,
            ["decrypt"]
        );
    }
    async function importPublicKey(jwKey) {
        return window.crypto.subtle.importKey(
            "jwk",
            jwKey,
            {name:"RSA-OAEP", hash:"SHA-256"},
            true,
            ["encrypt"]
        );
    }

    async function retrieveKey(user_name){
        const request = db.transaction('user', "readonly").objectStore('user').get(user_name);
        request.onsuccess = () =>{
            console.log(request.result.Prk)
            return importSecretKey(request.result.Prk);
        }
    }

});
