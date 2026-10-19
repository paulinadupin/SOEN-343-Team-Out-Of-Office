export const ROLES = {
  CUSTOMER: "CUSTOMER",
  RESTAURANT: "RESTAURANT",
  OPERATOR: "OPERATOR",
  COURIER: "COURIER",
};

export const HOME_BY_ACCOUNT_TYPE = {
  [ROLES.CUSTOMER]: "/",
  [ROLES.RESTAURANT]: "/dashboard",
  [ROLES.COURIER]: "/courier",
  [ROLES.OPERATOR]: "/operator",
};