export interface WorkListing {
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

interface Member {
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
