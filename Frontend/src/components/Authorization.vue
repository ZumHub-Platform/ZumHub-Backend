<script>
import functions from "../utils/functions.vue";
import auth from "../utils/authorization-utils.vue";
import sha1 from "js-sha1";
import * as EmailValidator from "email-validator";
let toNormalize = ["email", "password", "username", "password-repeat"];
let ac = "auth-container-";

function registerUser(username, email, password) {
  sha1(password);
  var hash_password = sha1.create();
  $.ajax({
    type: "HEAD",
    url: "http://localhost:1048/register",
    data: JSON.stringify(username),
    headers: {
      Authorization: "Basic " + btoa(email + ":" + hash_password),
    },
    success: function (data) {
      functions.methods.hide(["auth-window", "successCircle", "status"]);
      functions.methods.show(["success-window"]);
      functions.methods.changeText(
        "second-status",
        "Please check your email, for a confirmation."
      );
      setTimeout(() => {
        functions.methods.show(["successCircle"]);
      }, 200);
      setTimeout(() => {
        functions.methods.changeText("status", "Successfull registration");
        functions.methods.show(["status"]);
      }, 600);
    },
    error: function (err) {
      if (err.status === 409) {
        auth.methods.userAlreadyExists(ac + "email");
      }
    },
  });
}
function loginUser(email, password) {
  sha1(password);
  var hash_password = sha1.create();
  $.ajax({
    type: "GET",
    url: "http://localhost:1048/login",
    headers: {
      Authorization: "Basic " + btoa(email + ":" + hash_password),
    },
    success: function (data, status, xhr) {
      if (xhr.status === 200) {
        functions.methods.hide(["auth-window", "successCircle", "status"]);
        functions.methods.show(["success-window"]);
        functions.methods.changeText("second-status", "Redirecting...");
        setTimeout(() => {
          functions.methods.show(["successCircle"]);
        }, 200);
        setTimeout(() => {
          functions.methods.changeText("status", "Successfull login");
          functions.methods.show(["status"]);
        }, 600);
        let token = xhr.getResponseHeader("Authorization");
        sessionStorage.setItem(
          "token",
          token.slice(token.indexOf(" ") + 1, token.length)
        );
        /*         setTimeout(() => {
          window.location.href = "https://localhost:3000/#/main/";
        }, 3000); */
      }
    },
    error: function (err) {
      if (err.status === 404) {
        auth.methods.invalidEmail(ac + "email");
      }
      if (err.status === 401) {
        auth.methods.wrongPassword(ac + "password");
      }
    },
  });
}

export default {
  name: "Authorization",
  methods: {
    validateLogin() {
      var email = document.getElementById(ac + "email").value;
      var password = document.getElementById(ac + "password").value;
      let checkPassLength =
        password.length === 0
          ? auth.methods.fieldIsNull(ac + "password")
          : "no";

      if (this.validEmail(email) && checkPassLength === "no") {
        loginUser(email, password);
      }
    },
    validateSignup() {
      var email = document.getElementById(ac + "email").value;
      var password = document.getElementById(ac + "password").value;
      var username = document.getElementById(ac + "username").value;
      var passwordRepeat = document.getElementById(
        ac + "password-repeat"
      ).value;

      this.validUsername(username),
        this.validEmail(email),
        this.validPassword(password),
        this.validPasswordRepeat(password, passwordRepeat);

      while (
        this.validUsername(username) &&
        this.validEmail(email) &&
        this.validPassword(password) &&
        this.validPasswordRepeat(password, passwordRepeat)
      ) {
        registerUser(username, email, password);
        auth.methods.setNormal([
          "username",
          "email",
          "password",
          "password-repeat",
        ]);
        functions.methods.changeText("error-message", "");
        break;
      }
    },
    validate() {
      if (document.getElementById("auth-button").value === "Login") {
        functions.methods.resetAnimation("error-message");
        this.validateLogin();
      } else {
        this.validateSignup();
        functions.methods.resetAnimation("error-message");
      }
    },
    validUsername(username) {
      if (username.length === 0)
        return auth.methods.fieldIsNull(ac + "username");
      else {
        if (username.length < 4)
          return auth.methods.fieldIsUnderMinimum(ac + "username", 4);
        else if (username.length > 16)
          return auth.methods.fieldIsOverMaximum(ac + "username", 16);
        else auth.methods.setNormal(["username"]);
      }
      return true;
    },
    validEmail(email) {
      if (email.length === 0) return auth.methods.fieldIsNull(ac + "email");
      else {
        if (EmailValidator.validate(email) !== true)
          return auth.methods.wrongEmailSyntax(ac + "email");
        else auth.methods.setNormal(["email"]);
      }
      return true;
    },
    validPassword(password) {
      if (password.length === 0)
        return auth.methods.fieldIsNull(ac + "password");
      else {
        if (password.length < 8)
          return auth.methods.fieldIsUnderMinimum(ac + "password", 8);
        else auth.methods.setNormal(["password"]);
      }
      return true;
    },
    validPasswordRepeat(password, passwordRepeat) {
      if (passwordRepeat.length === 0)
        return auth.methods.fieldIsNull(ac + "password-repeat");
      else {
        if (passwordRepeat !== password)
          return auth.methods.passwordDoesntMatch(ac + "password-repeat");
        else if (password !== passwordRepeat)
          return auth.methods.passwordDoesntMatch(ac + "password-repeat");
        else auth.methods.setNormal(["password-repeat", "password"]);
      }
      return true;
    },
    showLogin() {
      let finalContainers = [];
      for (var i = 0; i < toNormalize.length; i++) {
        finalContainers.push(toNormalize[i]);
      }

      auth.methods.setActive("login-button", "signup-button");
      functions.methods.resetAnimation(ac + "inputs");
      functions.methods.changeTextAndValue("auth-button", "Login");

      functions.methods.hide([
        "username-container",
        "password-repeat-container",
      ]);

      functions.methods.changeText("error-message", "");
      auth.methods.setActive("login-button", "signup-button");
      functions.methods.shrink([ac.slice(0, -1), ac + "window"], "add");
      functions.methods.extend([ac.slice(0, -1), ac + "window"], "remove");
      for (var i = 0; i < finalContainers.length; i++) {
        auth.methods.setNormal([finalContainers[i]]);
      }
    },
    showSignup() {
      let finalContainers = [];
      for (var i = 0; i < toNormalize.length; i++) {
        finalContainers.push(toNormalize[i]);
      }
      functions.methods.resetAnimation(ac + "inputs");
      functions.methods.changeTextAndValue("auth-button", "Sign-up");
      functions.methods.show([
        "username-container",
        "password-repeat-container",
      ]);

      functions.methods.changeText("error-message", "");
      auth.methods.setActive("signup-button", "login-button");
      functions.methods.shrink([ac.slice(0, -1), ac + "window"], "remove");
      functions.methods.extend([ac.slice(0, -1), ac + "window"], "add");

      for (var i = 0; i < finalContainers.length; i++) {
        auth.methods.setNormal([finalContainers[i]]);
      }
    },
  },
  created() {
    setTimeout(this.showLogin, 100);
  },
};
</script>

<template>
  <html lang="en">
    <body>
      <div>
        <h1
          id="title"
          class="font-bold text-8xl font-raleway top-8 relative select-none"
        >
          ZumHub
        </h1>
      </div>
      <div id="auth-container" class="container mx-auto relative top-32 w-96">
        <div
          id="auth-container-window"
          class="bg-white rounded-2xl container w-96 mx-auto shadow-2xl"
        >
          <div
            id="success-window"
            class="w-96 animate-rollDownSuccess bg-gray-50 rounded-2xl"
            style="display: none"
          >
            <div
              id="successCircle"
              class="circle-wrap absolute left-[7rem] dropdown"
            >
              <div class="circle dropshadoworange">
                <div class="mask full">
                  <div class="fill"></div>
                </div>

                <div class="mask half">
                  <div class="fill"></div>
                </div>

                <div class="inside-circle">
                  <div
                    class="
                      absolute
                      w-12
                      h-12
                      left-10
                      top-10
                      animate-scale
                      dropshadowlime
                    "
                  >
                    <svg
                      class="fill-green-400"
                      xmlns="http://www.w3.org/2000/svg"
                      viewBox="0 0 448 512"
                    >
                      <path
                        d="M438.6 105.4C451.1 117.9 451.1 138.1 438.6 150.6L182.6 406.6C170.1 419.1 149.9 419.1 137.4 406.6L9.372 278.6C-3.124 266.1-3.124 245.9 9.372 233.4C21.87 220.9 42.13 220.9 54.63 233.4L159.1 338.7L393.4 105.4C405.9 92.88 426.1 92.88 438.6 105.4H438.6z"
                      />
                    </svg>
                  </div>
                </div>
              </div>
            </div>
            <div
              id="status"
              class="
                absolute
                container
                mx-auto
                top-8
                font-raleway
                text-xl
                font-bold
                select-none
                animate-fadeIn
              "
            ></div>
            <div
              id="second-status"
              class="
                absolute
                container
                mx-auto
                bottom-8
                font-raleway
                text-xl
                font-bold
                select-none
                animate-pulse
              "
            ></div>
          </div>
          <div id="auth-window">
            <div id="auth-container-buttons" class="grid gap-0.5 grid-cols-2">
              <div>
                <button
                  type="button"
                  id="login-button"
                  @click="showLogin()"
                  class="
                    rounded-tl-2xl
                    bg-slate-200
                    h-12
                    w-48
                    hover:bg-amber-400
                    font-bold
                    text-xl
                    transition
                    duration-500
                    ease-out
                    z-10
                  "
                >
                  Login
                </button>
              </div>
              <div>
                <button
                  type="button"
                  id="signup-button"
                  @click="showSignup()"
                  class="
                    rounded-tr-2xl
                    bg-slate-200
                    h-12
                    w-48
                    hover:bg-amber-400
                    font-bold
                    text-xl
                    transition
                    duration-500
                    ease-out
                    z-10
                  "
                >
                  Sign-up
                </button>
              </div>
              <div
                id="error-message"
                style="color: red"
                class="
                  absolute
                  text-sm
                  container
                  mx-auto
                  w-96
                  top-12
                  bg-red-200
                  animate-fadeIn
                "
                value=""
              ></div>
            </div>
            <form
              id="auth-container-inputs"
              class="
                flex flex-col
                gap-4
                justify-center
                align-items-center
                mx-12
                mt-8
                animate-fadeIn
              "
            >
              <div id="username-container" class="flex flex-col">
                <label
                  id="auth-container-username-text"
                  for="auth-container-username"
                  class="flex justify-start text-lg font-bold"
                  >Username<span class="ml-1" style="color: red">*</span></label
                >
                <input
                  id="auth-container-username"
                  type="text"
                  class="
                    bg-slate-100
                    w-72
                    h-12
                    rounded-3xl
                    border-2
                    text-xl
                    indent-2.5
                  "
                />
              </div>
              <div
                id="email-container"
                class="flex flex-col flex-shrink-0 space-y-1"
              >
                <label
                  id="auth-container-email-text"
                  for="auth-container-email"
                  class="flex justify-start text-lg font-bold"
                  >Email<span class="ml-1" style="color: red">*</span></label
                >
                <input
                  id="auth-container-email"
                  type="text"
                  class="
                    bg-slate-100
                    w-72
                    h-12
                    rounded-3xl
                    border-2
                    text-xl
                    indent-2.5
                  "
                />
              </div>
              <div
                id="password-container"
                class="flex flex-col flex-shrink-0 space-y-1"
              >
                <label
                  for="auth-container-password"
                  class="flex justify-start text-lg font-bold"
                  >Password<span class="ml-1" style="color: red">*</span></label
                >
                <input
                  id="auth-container-password"
                  type="password"
                  class="
                    bg-slate-100
                    w-72
                    h-12
                    rounded-3xl
                    border-2
                    text-xl
                    indent-2.5
                  "
                />
              </div>
              <div
                id="password-repeat-container"
                class="flex flex-col flex-shrink-0 space-y-1"
              >
                <label
                  id="auth-container-password-repeat-text"
                  for="auth-container-password-repeat"
                  class="flex justify-start text-lg font-bold"
                  >Repeat Password<span class="ml-1" style="color: red"
                    >*</span
                  ></label
                >
                <input
                  id="auth-container-password-repeat"
                  type="password"
                  class="
                    bg-slate-100
                    w-72
                    h-12
                    rounded-3xl
                    border-2
                    text-xl
                    indent-2.5
                  "
                />
              </div>
            </form>
            <div
              id="confirm-button"
              class="bottom-6 absolute right-0 container mx-auto"
            >
              <button
                type="button"
                @click="validate()"
                value=""
                id="auth-button"
                class="
                  rounded-3xl
                  w-72
                  h-12
                  bg-amber-300
                  text-xl
                  font-bold
                  hover:bg-amber-400
                "
              ></button>
            </div>
          </div>
        </div>
      </div>
    </body>
  </html>
</template>

<style>
.extend {
  height: 538px;
  transition: height 0.5s ease-out;
}

.shrink {
  height: 350px;
  transition: height 0.5s ease-out;
}

.warning {
  border: 1px solid red !important;
  transition: border ease-in-out 0.8s;
}

.dropshadowlime {
  filter: drop-shadow(1px 1px 3px lime);
}

.dropshadoworange {
  filter: drop-shadow(1px 1px 3px orange);
}

.circle-wrap {
  width: 150px;
  height: 150px;
  background: #e6e2e7;
  border-radius: 50%;
  animation: dropdown forwards ease-in-out 1s;
  top: 0px;
}

.circle-wrap .circle .mask,
.circle-wrap .circle .fill {
  width: 150px;
  height: 150px;
  position: absolute;
  border-radius: 50%;
}

.circle-wrap .circle .mask {
  clip: rect(0px, 150px, 150px, 75px);
}

.circle-wrap .circle .mask .fill {
  clip: rect(0px, 75px, 150px, 0px);
  background-color: #fcd34d;
}
.circle-wrap .circle .mask.full,
.circle-wrap .circle .fill {
  animation: fill ease-in-out 1s;
  transform: rotate(180deg);
}

@keyframes fill {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(180deg);
  }
}

@keyframes dropdown {
  0% {
    top: 0px;
  }
  100% {
    top: 90px;
  }
}

.circle-wrap .inside-circle {
  width: 130px;
  height: 130px;
  border-radius: 50%;
  line-height: 130px;
  background-color: white;
  text-align: center;
  margin-top: 10px;
  margin-left: 10px;
  position: absolute;
  z-index: 100;
  font-weight: 700;
  font-size: 2em;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
</style>
