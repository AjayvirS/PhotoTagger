/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const {setGlobalOptions} = require("firebase-functions");
const {onRequest} = require("firebase-functions/https");
const {GoogleGenAI} = require("@google/genai");
const logger = require("firebase-functions/logger");

// For cost control, you can set the maximum number of containers that can be
// running at the same time. This helps mitigate the impact of unexpected
// traffic spikes by instead downgrading performance. This limit is a
// per-function limit. You can override the limit for each function using the
// `maxInstances` option in the function's options, e.g.
// `onRequest({ maxInstances: 5 }, (req, res) => { ... })`.
// NOTE: setGlobalOptions does not apply to functions using the v1 API. V1
// functions should each use functions.runWith({ maxInstances: 10 }) instead.
// In the v1 API, each function can only serve one request per container, so
// this will be the maximum concurrent request count.
setGlobalOptions({maxInstances: 5});

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

const ai = new GoogleGenAI({apiKey: process.env.GEMINI_API_KEY});
const model = "gemini-2.5-flash";


exports.annotateArtwork = onRequest({
  region: "europe-west1", secrets: ["GEMINI_API_KEY"],
}, async (req, res) => {
  try {
    if (req.method !== "POST") {
      return res.status(405).json({error: "Only POST allowed"});
    }
    const {image64, mimeType, prompt} = req.body || {};
    if (!image64 || !mimeType) {
      return res.status(400).json({
        error: "Missing or Broken Image," +
          " please try again and upload a valid image.",
      });
    }

    const instruction = prompt || "Write a concise title/caption of what you see in the image. " +
      "If you are unsure, say so. \n" +
      "Return a simple text (string), do not reference the fact that it is a photograph about..., just a short caption is fine";

    const result = await ai.models.generateContent({
      model, contents: [{
        role: "user",
        parts: [
          {text: instruction},
          {
            inlineData: {
              mimeType, data: image64,
            },
          },
        ],
      }],
    });
    const text = result.text || "";
    return res.status(200).json({raw: text});
  } catch (e) {
    logger.error(e);
    return res.status(500).json({error: "500 Internal Error"});
  }
});
