// src/app/shared/validators/password.validator.ts
import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class PasswordValidators {
  /**
   * Validates that two fields match (e.g., password and confirm password)
   * @param controlName Primary control name
   * @param matchingControlName Control to compare against
   * @param errorKey Custom error key (default: 'mismatch')
   */
  static match(
    controlName: string, 
    matchingControlName: string,
    errorKey: string = 'mismatch'
  ): ValidatorFn {
    return (formGroup: AbstractControl): ValidationErrors | null => {
      const control = formGroup.get(controlName);
      const matchingControl = formGroup.get(matchingControlName);

      // Skip validation if controls not found
      if (!control || !matchingControl) {
        console.warn(`PasswordValidator: Could not find controls ${controlName} or ${matchingControlName}`);
        return null;
      }

      // Clear previous mismatch error
      if (matchingControl.hasError(errorKey)) {
        delete matchingControl.errors![errorKey];
        matchingControl.updateValueAndValidity({ onlySelf: true });
      }

      // Set new error if values don't match
      if (control.value !== matchingControl.value) {
        matchingControl.setErrors({ 
          ...matchingControl.errors, 
          [errorKey]: true 
        });
        return { [errorKey]: true };
      }

      return null;
    };
  }
}