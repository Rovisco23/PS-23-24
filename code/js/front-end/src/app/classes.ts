export interface Classes {
  id: string;
  name: string;
  description: string;
  address: Address;
  type: string;
  state: string;
}

interface Address {
  location: Location,
  street: String,
  postalCode: String
}

interface Location {
  district: String,
  county: String,
  parish: String
}

export interface Member {
  id: number;
  name: string;
  role: string;
}

export interface Work {
  id: string;
  name: string;
  description: string;
  address: Address;
  type: string;
  state: string;
  members: Member[];
}

export interface User {
  id: String,
  username: String,
  email: String,
  phone: String | null,
  firstName: String,
  lastName: String,
  role: String,
  location: Location
}

