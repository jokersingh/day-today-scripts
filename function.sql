-- Create table1
CREATE TABLE table1 (
  vin_first_three_char VARCHAR(3) PRIMARY KEY,
  default_mfg_country VARCHAR(4) DEFAULT NULL,
  is_world_mfg BOOLEAN DEFAULT FALSE
);

-- Create table2
CREATE TABLE table2 (
  mfg_country_code VARCHAR(2) PRIMARY KEY,
  mfg_country_name VARCHAR(4) NOT NULL
);

-- Function to get manufacturing country based on VIN
CREATE OR REPLACE FUNCTION get_mfg_country(vin VARCHAR)
RETURNS VARCHAR AS $$
BEGIN
  -- Extract first 3 characters and handle potential missing 11th char
  DECLARE first_part VARCHAR(3);
  SET first_part = LEFT(vin, 3);
  DECLARE eleventh_char CHAR(1);
  BEGIN
    SET eleventh_char = SUBSTRING(vin, 11, 1);
  EXCEPTION WHEN DATA_TRUNCATION THEN
    eleventh_char := NULL; -- Set to NULL if 11th character doesn't exist
  END;

  -- Query table1 based on first 3 characters
  SELECT CASE WHEN is_world_mfg THEN NULL ELSE default_mfg_country END AS mfg_country
  FROM table1
  WHERE vin_first_three_char = first_part;

  IF FOUND THEN
    RETURN; -- Exit function if data found in table1 (non-world mfg)
  END IF;

  -- If not found or world mfg, check 11th char and query table2
  IF eleventh_char IS NOT NULL THEN
    SELECT mfg_country_code
    FROM table2
    WHERE mfg_country_code = eleventh_char;
  ELSE
    -- Return default_mfg_country if 11th char missing
    RETURN (SELECT default_mfg_country FROM table1 WHERE vin_first_three_char = first_part);
  END IF;

  -- No data found in either table - return UNK
  RETURN 'UNK';
END;
$$ LANGUAGE plpgsql;
