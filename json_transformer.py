import json

def read_json(file_path):
    with open(file_path, 'r') as file:
        data = json.load(file)
    return data

def split_and_read_attributes(json_data):
    for key, value in json_data.items():
        if isinstance(value, dict):
            print(f"Object: {key}")
            for inner_key, inner_value in value.items():
                print(f"  {inner_key}: {inner_value}")
        elif isinstance(value, list):
            print(f"List: {key}")
            for obj in value:
                for inner_key, inner_value in obj.items():
                    print(f"  {inner_key}: {inner_value}")

def transform_and_create_mapping(json_data):
    transformed_data = {}
    
    transformed_data['C'] = {
        'Object1': json_data['A']['Object1'],
        'key1': json_data['A']['key1']
    }

    transformed_data['D'] = []
    for obj in json_data['B']:
        if obj['key11'] == 'SQL':
            transformed_obj = {
                'Object11': obj['Object11'],
                'key11': 'READ_JDBC'
            }
            transformed_data['D'].append(transformed_obj)

    return transformed_data

if __name__ == "__main__":
    # Step 1: Read JSON
    file_path = 'your_json_file.json'
    json_data = read_json(file_path)

    # Step 2: Split JSON object and read its attributes
    split_and_read_attributes(json_data)

    # Step 3: Transform into another JSON based upon mapping
    # Step 4: Create mapping of JSON object
    transformed_data = transform_and_create_mapping(json_data)

    # Display the transformed data
    print("\nTransformed Data:")
    print(json.dumps(transformed_data, indent=2))
