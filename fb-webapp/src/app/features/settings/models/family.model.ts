export type FamilyRole = 'OWNER' | 'FULL_ACCESS' | 'INFO_ONLY';

export interface Family {
  id: string;
  name: string;
  description?: string;
  ownerId: string;
  ownerUsername?: string;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  updatedBy?: string;
  memberCount?: number;
}

export interface FamilyMember {
  id: string;
  familyId: string;
  userId: string;
  username?: string;
  email?: string;
  role: FamilyRole;
  createdAt?: string;
  createdBy?: string;
}

export interface CreateFamilyRequest {
  name: string;
  description?: string;
}

export interface UpdateFamilyRequest {
  name: string;
  description?: string;
}

export interface AddMemberRequest {
  userId: string;
  role: FamilyRole;
}
