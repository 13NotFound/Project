function like(postId) {
  const likeCount = document.getElementById(`likes-count-${postId}`);
  const likeButton = document.getElementById(`like-button-${postId}`);

  fetch(`/like-post/${postId}`, { method: "POST" })
      .then((res) => res.json())
      .then((data) => {
        likeCount.innerHTML = data["likes"];
        if (data["liked"] === true) {
          likeButton.className = "fas fa-thumbs-up";
        } else {
          likeButton.className = "far fa-thumbs-up";
        }
      })
      .catch((e) => alert("Could not like post."));
}
function dislike(postId) {
    const dislikeCount = document.getElementById(`dislikes-count-${postId}`);
    const dislikeButton = document.getElementById(`dislike-button-${postId}`);

    fetch(`/dislike-post/${postId}`, { method: "POST" })
        .then((res) => res.json())
        .then((data) => {
            dislikeCount.innerHTML = data["dislikes"];
            if (data["disliked"] === true) {
                dislikeButton.className = "fas fa-thumbs-down";
            } else {
                dislikeButton.className = "far fa-thumbs-down";
            }
        })
        .catch((e) => alert("Could not dislike post."));
}

function mute(userID) {
    fetch(`/mute-user/${userID}`, { method: "POST" }).then(location.reload()).catch((e) => alert("Could not mute user."));
}

function unmute(userID) {
    fetch(`/unmute-user/${userID}`, { method: "POST" }).then(location.reload()).catch((e) => alert("Could not unmute user."));
    location.reload();
}

function send_post(E){
    fetch(`/create-post`,{
        method: "POST",
        body: JSON.stringify({text: E.getHtml(),
            anon: document.getElementById("anon").value,
            adminOnly: document.getElementById("adminOnly").value,
            course:document.getElementById("course").value,
            topic:document.getElementById("topic").value}),
        }).then((_res) => {
             location.reload();
        }
    )
}

