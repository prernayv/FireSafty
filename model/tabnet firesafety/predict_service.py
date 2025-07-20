import joblib
import numpy as np
import json
import sys
import os

def predict_safety(data):
    try:
        # Get the directory where this script is located
        script_dir = os.path.dirname(os.path.abspath(__file__))
        
        # Load saved model and preprocessing tools using absolute paths
        clf = joblib.load(os.path.join(script_dir, "tabnet_model.pkl"))
        building_type_encoder = joblib.load(os.path.join(script_dir, "building_type_encoder.pkl"))
        status_encoder = joblib.load(os.path.join(script_dir, "status_encoder.pkl"))

        # Convert input into feature array
        user_building_type_encoded = building_type_encoder.transform([data["building_type"]])[0]

        user_features = np.array([[
            user_building_type_encoded,
            data["floors"],
            data["rooms"],
            data["capacity"],
            data["area"],
            data["extinguishers"],
            data["sprinklers"],
            data["smoke_detectors"],
            data["fire_alarms"]
        ]])

        # Predict using the model (without scaling)
        prediction = clf.predict(user_features)[0]
        predicted_label = status_encoder.inverse_transform([prediction])[0]

        return {
            "status": predicted_label,
            "is_safe": predicted_label.lower() == "safe",
            "confidence": float(clf.predict_proba(user_features)[0][prediction])
        }

    except Exception as e:
        return {"error": str(e)}

if __name__ == "__main__":
    # Read input from stdin
    input_data = json.loads(sys.stdin.read())
    result = predict_safety(input_data)
    print(json.dumps(result)) 