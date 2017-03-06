#pragma once

template<typename T> struct Nullable {
	T value;
	bool is_empty_value = true;

	Nullable() { this->is_empty_value = true; }

	Nullable(T value) : value(value) {
		this->is_empty_value = false;
	}

	inline T& getValue() {
		if (this->is_empty_value) {
			throw std::exception("empty value access.");
		}
		return this->value;
	}

	inline T& getValueOrDefault(T defaultValue) {
		T __return = defaultValue;
		if (!this->is_empty_value) {
			__return = this->value;
		}
		return __return;
	}

	inline T& operator*() {
		return this->getValue();
	}
	inline bool has_value() { return this->is_empty_value; }
};
